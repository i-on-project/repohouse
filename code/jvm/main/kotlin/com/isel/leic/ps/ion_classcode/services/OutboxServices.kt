package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.input.OtpInput
import com.isel.leic.ps.ion_classcode.domain.input.OutboxInput
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.services.EmailService
import com.isel.leic.ps.ion_classcode.utils.Either
import com.isel.leic.ps.ion_classcode.utils.Result
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.sql.Timestamp

/**
 * Alias for the response of the services
 */
typealias OutboxResponse = Either<OutboxServicesError, Unit>

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
        if (userId <= 0) return Either.Left(value = OutboxServicesError.InvalidInput)
        val otp = createRandomOtp()
        return transactionManager.run {
            val cooldown = it.cooldownRepository.getCooldownRequest(userId = userId)
            if (cooldown != null) {
                return@run Either.Left(value = OutboxServicesError.CooldownNotExpired(cooldown = cooldown))
            }
            val otpRequest = it.otpRepository.createOtpRequest(otp = OtpInput(userId = userId, otp = otp))
            if (otpRequest == null) {
                Either.Left(value = OutboxServicesError.ErrorCreatingRequest)
            }
            val outbox = it.outboxRepository.createOutboxRequest(outbox = OutboxInput(userId = userId))
            if (outbox == null) {
                Either.Left(value = OutboxServicesError.ErrorCreatingRequest)
            } else {
                Either.Right(value = Unit)
            }
        }
    }

    /**
     * Method to check the otp
     */
    fun checkOtp(userId: Int, otp: Int): OutboxResponse {
        if (userId <= 0 || otp <= 0) return Either.Left(value = OutboxServicesError.InvalidInput)
        return transactionManager.run {
            val cooldown = it.cooldownRepository.getCooldownRequest(userId = userId)
            if (cooldown != null) {
                return@run Either.Left(value = OutboxServicesError.CooldownNotExpired(cooldown = cooldown))
            }
            var otpRequest = it.otpRepository.getOtpRequest(userId = userId) ?: return@run Either.Left(value = OutboxServicesError.OtpNotFound)
            if (otpRequest.expiredAt.before(System.currentTimeMillis().toTimestamp())) {
                it.outboxRepository.deleteOutboxRequest(userId = userId)
                it.otpRepository.deleteOtpRequest(userId = userId)
                return@run Either.Left(value = OutboxServicesError.OtpExpired)
            }
            if (otpRequest.otp == otp) {
                it.usersRepository.updateUserStatus(id = userId)
                return@run Either.Right(value = Unit)
            }
            if (otpRequest.tries == MAX_TRIES) {
                it.cooldownRepository.createCooldownRequest(userId = userId, endTime = addTime())
                return@run Either.Left(value = OutboxServicesError.CooldownNotExpired(cooldown = COOLDOWN_TIME))
            } else {
                while (otpRequest.tries < MAX_TRIES) {
                    val otpTry = it.otpRepository.addTryToOtpRequest(userId = userId, tries = otpRequest.tries + 1)
                    if (otpTry) {
                        break
                    }
                    otpRequest = it.otpRepository.getOtpRequest(userId = userId) ?: return@run Either.Left(value = OutboxServicesError.OtpNotFound)
                }
                return@run Either.Left(value = OutboxServicesError.OtpDifferent)
            }
        }
    }

    /**
     * Method to resend the email
     */
    fun resendEmail(userId: Int): OutboxResponse {
        if (userId <= 0) return Either.Left(value = OutboxServicesError.InvalidInput)
        return transactionManager.run {
            val outbox = it.outboxRepository.getOutboxRequest(userId = userId) ?: return@run Either.Left(value = OutboxServicesError.OtpNotFound)
            if (outbox.sentAt == null) {
                /** Not yet sent, needs to wait */
                return@run Either.Right(value = Unit)
            }
            if (outbox.sentAt.before((System.currentTimeMillis() + EMAIL_RESEND_TIME).toTimestamp())) {
                return@run Either.Left(value = OutboxServicesError.EmailNotSent)
            }
            /** Changes state, so next verification can be sent */
            it.outboxRepository.updateOutboxStateRequest(userId = outbox.userId, state = "Pending")
            return@run Either.Right(value = Unit)
        }
    }

    /**
     * Method scheduled to send the emails
     */
    @Scheduled(fixedRate = 10000)
    fun sendEmails() {
        transactionManager.run {
            it.outboxRepository.getOutboxPendingRequests().forEach { outbox ->
                it.usersRepository.getUserById(userId = outbox.userId)?.let { user ->
                    it.otpRepository.getOtpRequest(userId = outbox.userId)?.let { otpRequest ->
                        if (emailService.sendVerificationEmail(
                                name = user.name,
                                email = user.email,
                                otp = otpRequest.otp,
                            ) is Result.Success
                        ) {
                            it.outboxRepository.updateOutboxStateRequest(userId = outbox.userId, state = "Sent")
                            it.outboxRepository.updateOutboxSentTimeRequest(userId = outbox.userId)
                        }
                    }
                }
            }
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
