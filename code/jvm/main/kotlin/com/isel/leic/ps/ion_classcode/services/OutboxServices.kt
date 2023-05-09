package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.Outbox
import com.isel.leic.ps.ion_classcode.domain.input.OtpInput
import com.isel.leic.ps.ion_classcode.domain.input.OutboxInput
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.sql.Timestamp

/**
 * Alias for the response of the services
 */
typealias OutboxResponse = Result<OutboxServicesError, Outbox>
typealias UpdateOutboxResponse = Result<OutboxServicesError, Unit>

const val EMAIL_RESEND_TIME = 300000 // 5-minutes cooldown
const val MAX_TRIES = 3

/**
 * Services for the outbox
 */
sealed class OutboxServicesError {
    object UserNotFound : OutboxServicesError()
    object OtpNotFound : OutboxServicesError()
    object OtpExpired : OutboxServicesError()
    object OtpDifferent : OutboxServicesError()
    object EmailNotSent : OutboxServicesError()
    object ErrorCreatingRequest : OutboxServicesError()
    object InvalidInput : OutboxServicesError()
    object InternalError : OutboxServicesError()
    class CooldownNotExpired(val cooldown: Int) : OutboxServicesError()
}

private const val COOLDOWN_TIME = 500000 // 5-minutes cooldown

/**
 * Service to the outbox services
 */
@Component
class OutboxServices(
    private val transactionManager: TransactionManager,
    private val emailService: EmailService,
) {

    /**
     * Method to create a new outbox request
     */
    fun createUserVerification(userId: Int): OutboxResponse {
        if (userId <= 0) return Result.Problem(OutboxServicesError.InternalError)
        val otp = createRandomOtp()
        return transactionManager.run {
            val cooldown = it.cooldownRepository.getCooldownRequestRemainingTime(userId)
            if (cooldown != null) return@run Result.Problem(OutboxServicesError.CooldownNotExpired(cooldown))
            it.otpRepository.createOtpRequest(OtpInput(userId, otp))
            val outbox = it.outboxRepository.createOutboxRequest(OutboxInput(userId))
            Result.Success(outbox)
        }
    }

    /**
     * Method to check the otp
     */
    fun checkOtp(userId: Int, otp: Int): UpdateOutboxResponse {
        if (otp <= 0) return Result.Problem(OutboxServicesError.InvalidInput)
        return transactionManager.run {
            it.usersRepository.getStudent(userId) ?: return@run Result.Problem(OutboxServicesError.InternalError)
            val cooldown = it.cooldownRepository.getCooldownRequestRemainingTime(userId)
            if (cooldown != null) return@run Result.Problem(OutboxServicesError.CooldownNotExpired(cooldown))
            var otpRequest = it.otpRepository.getOtpRequest(userId) ?: return@run Result.Problem(OutboxServicesError.OtpNotFound)
            if (otpRequest.expiredAt.before(System.currentTimeMillis().toTimestamp())) {
                it.outboxRepository.deleteOutboxRequest(userId)
                it.otpRepository.deleteOtpRequest(userId)
                return@run Result.Problem(OutboxServicesError.OtpExpired)
            }
            if (otpRequest.otp == otp) {
                it.usersRepository.updateUserStatus(userId)
                return@run Result.Success(Unit)
            }
            if (otpRequest.tries == MAX_TRIES) {
                it.cooldownRepository.createCooldownRequest(userId, addTime())
                return@run Result.Problem(OutboxServicesError.CooldownNotExpired(COOLDOWN_TIME))
            } else {
                while (otpRequest.tries < MAX_TRIES) {
                    val otpTry = it.otpRepository.addTryToOtpRequest(userId, otpRequest.tries + 1)
                    if (otpTry) break
                    otpRequest = it.otpRepository.getOtpRequest(userId) ?: return@run Result.Problem(OutboxServicesError.OtpNotFound)
                }
                return@run Result.Problem(OutboxServicesError.OtpDifferent)
            }
        }
    }

    /**
     * Method to resend the email
     */
    fun resendEmail(userId: Int): UpdateOutboxResponse {
        if (userId <= 0) return Result.Problem(OutboxServicesError.InternalError)
        return transactionManager.run {
            val outbox = it.outboxRepository.getOutboxRequest(userId) ?: return@run Result.Problem(OutboxServicesError.OtpNotFound)
            if (outbox.sentAt == null) {
                /** Not yet sent, needs to wait */
                return@run Result.Success(Unit)
            }
            if (outbox.sentAt.before((System.currentTimeMillis() + EMAIL_RESEND_TIME).toTimestamp())) {
                return@run Result.Problem(OutboxServicesError.EmailNotSent)
            }
            /** Changes state, so next verification can be sent */
            it.outboxRepository.updateOutboxStateRequest(outbox.userId, "Pending")
            return@run Result.Success(Unit)
        }
    }

    /**
     * Method scheduled to send the emails
     */
    @Scheduled(fixedRate = 10000)
    fun sendEmails() {
        transactionManager.run {
            it.outboxRepository.getOutboxPendingRequests().forEach { outbox ->
                it.usersRepository.getUserById(outbox.userId)?.let { user ->
                    it.otpRepository.getOtpRequest(outbox.userId)?.let { otpRequest ->
                        if (emailService.sendVerificationEmail(user.name, user.email, otpRequest.otp) is Result.Success) {
                            it.outboxRepository.updateOutboxStateRequest(outbox.userId, "Sent")
                            it.outboxRepository.updateOutboxSentTimeRequest(outbox.userId)
                        }
                    }
                }
            }
        }
    }

    /**
     * Function to handle errors about the OTP.
     */
    fun problem(error: OutboxServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            OutboxServicesError.OtpExpired -> Problem.gone
            OutboxServicesError.OtpDifferent -> Problem.invalidInput
            OutboxServicesError.OtpNotFound -> Problem.notFound
            OutboxServicesError.UserNotFound -> Problem.notFound
            OutboxServicesError.EmailNotSent -> Problem.internalError
            OutboxServicesError.ErrorCreatingRequest -> Problem.internalError
            OutboxServicesError.InvalidInput -> Problem.invalidInput
            OutboxServicesError.InternalError -> Problem.internalError
            else -> Problem.cooldown((error as OutboxServicesError.CooldownNotExpired).cooldown)
        }
    }

    /**
     * Method to create a random otp
     */
    private fun createRandomOtp(): Int {
        return (100000..999999).random()
    }

    /**
     * Method to convert a long to a timestamp
     */
    private fun Long.toTimestamp(): Timestamp {
        return Timestamp(this)
    }

    /**
     * Method to add the cooldown time
     */
    private fun addTime(): Timestamp {
        return (System.currentTimeMillis() + COOLDOWN_TIME).toTimestamp()
    }
}
