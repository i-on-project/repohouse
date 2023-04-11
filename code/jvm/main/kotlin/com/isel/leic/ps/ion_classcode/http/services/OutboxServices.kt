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
    class CooldownNotExpired(val cooldown: Int) : OutboxServicesError()
}

private const val COOLDOWN_TIME = 500000 // 5 minutes cooldown

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
        val otp = createRandomOtp()
        return transactionManager.run {
            val cooldown = it.cooldownRepository.getCooldownRequest(userId)
            if (cooldown != null) {
                Either.Left(OutboxServicesError.CooldownNotExpired(cooldown))
            }
            val outbox = it.outboxRepository.createOutboxRequest(OutboxInput(userId, otp))
            if (outbox == null) {
                Either.Left(OutboxServicesError.ErrorCreatingRequest)
            } else {
                Either.Right(Unit)
            }
        }
    }

    /**
     * Method to check the otp
     */
    fun checkOtp(userId: Int, otp: Int): OutboxResponse {
        return transactionManager.run {
            val outbox = it.outboxRepository.getOutboxRequest(userId)
            if (outbox == null) {
                Either.Left(OutboxServicesError.OtpNotFound)
            } else {
                if (outbox.expiredAt.before(System.currentTimeMillis().toTimestamp())) {
                    it.outboxRepository.deleteOutboxRequest(outbox.userId)
                    Either.Left(OutboxServicesError.OtpExpired)
                }
                if (outbox.otp == otp) {
                    it.usersRepository.updateUserStatus(userId)
                    Either.Right(Unit)
                } else {
                    it.outboxRepository.deleteOutboxRequest(outbox.userId)
                    it.cooldownRepository.createCooldownRequest(userId, addTime())
                    Either.Left(OutboxServicesError.OtpDifferent)
                }
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
                it.usersRepository.getUserById(outbox.userId)?.let { user ->
                    if (emailService.sendVerificationEmail(user.name, user.email, outbox.otp) is Either.Right) {
                        it.outboxRepository.updateOutboxStateRequest(outbox.userId)
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
