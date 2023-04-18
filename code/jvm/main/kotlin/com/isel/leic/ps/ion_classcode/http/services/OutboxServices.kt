package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.input.OutboxInput
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.sql.Timestamp

/**
 * Alias for the response of the services
 */
typealias OutboxResponse = Either<OutboxServicesError, Unit>

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

private const val COOLDOWN_TIME = 5000 // 5 minutes cooldown

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
            val outbox = it.outboxRepository.createOutboxRequest(outbox = OutboxInput(userId = userId, otp = otp))
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
            val outbox = it.outboxRepository.getOutboxRequest(userId = userId) ?: return@run Either.Left(value = OutboxServicesError.OtpNotFound)
            if (outbox.expiredAt.before(System.currentTimeMillis().toTimestamp())) {
                it.outboxRepository.deleteOutboxRequest(userId = outbox.userId)
                return@run Either.Left(value = OutboxServicesError.OtpExpired)
            }
            if (outbox.otp == otp) {
                it.usersRepository.updateUserStatus(id = userId)
                Either.Right(value = Unit)
            } else {
                //it.outboxRepository.deleteOutboxRequest(userId = outbox.userId) TODO: CHECK THIS
                it.cooldownRepository.createCooldownRequest(userId = userId, endTime = addTime())
                Either.Left(value = OutboxServicesError.OtpDifferent)
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
                it.usersRepository.getUserById(id = outbox.userId)?.let { user ->
                    if (emailService.sendVerificationEmail(name = user.name, email = user.email, otp = outbox.otp) is Either.Right) {
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
