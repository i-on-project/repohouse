package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.input.OutboxInput
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Result
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.sql.Timestamp

/**
 * Alias for the response of the services
 */
typealias OutboxResponse = Result<OutboxServicesError, Unit>

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
        if (userId <= 0) return Result.Problem(value = OutboxServicesError.InvalidInput)
        val otp = createRandomOtp()
        return transactionManager.run {
            val cooldown = it.cooldownRepository.getCooldownRequest(userId = userId)
            if (cooldown != null) {
                return@run Result.Problem(value = OutboxServicesError.CooldownNotExpired(cooldown = cooldown))
            }
            val outbox = it.outboxRepository.createOutboxRequest(outbox = OutboxInput(userId = userId, otp = otp))
            if (outbox == null) {
                Result.Problem(value = OutboxServicesError.ErrorCreatingRequest)
            } else {
                Result.Success(value = Unit)
            }
        }
    }

    /**
     * Method to check the otp
     */
    fun checkOtp(userId: Int, otp: Int): OutboxResponse {
        if (userId <= 0 || otp <= 0) return Result.Problem(value = OutboxServicesError.InvalidInput)
        return transactionManager.run {
            val cooldown = it.cooldownRepository.getCooldownRequest(userId = userId)
            if (cooldown != null) {
                return@run Result.Problem(value = OutboxServicesError.CooldownNotExpired(cooldown = cooldown))
            }
            val outbox = it.outboxRepository.getOutboxRequest(userId = userId) ?: return@run Result.Problem(value = OutboxServicesError.OtpNotFound)
            if (outbox.expiredAt.before(System.currentTimeMillis().toTimestamp())) {
                it.outboxRepository.deleteOutboxRequest(userId = outbox.userId)
                return@run Result.Problem(value = OutboxServicesError.OtpExpired)
            }
            if (outbox.otp == otp) {
                it.usersRepository.updateUserStatus(id = userId)
                Result.Success(value = Unit)
            } else {
                //it.outboxRepository.deleteOutboxRequest(userId = outbox.userId) TODO: Check this, maybe delete after expiration
                it.cooldownRepository.createCooldownRequest(userId = userId, endTime = addTime())
                Result.Problem(value = OutboxServicesError.OtpDifferent)
            }
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
                    if (emailService.sendVerificationEmail(name = user.name, email = user.email, otp = outbox.otp) is Result.Success) {
                        it.outboxRepository.updateOutboxStateRequest(userId = outbox.userId)
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
