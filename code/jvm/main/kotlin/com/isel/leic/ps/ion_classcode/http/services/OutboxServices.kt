package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.input.OutboxInput
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import java.sql.Timestamp
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


typealias OutboxResponse = Either<OutboxServicesError, Unit>

sealed class OutboxServicesError {
    object UserNotFound : OutboxServicesError()
    object OtpNotFound : OutboxServicesError()
    object OtpExpired : OutboxServicesError()
    object OtpDifferent : OutboxServicesError()
    object EmailNotSent : OutboxServicesError()
    object ErrorCreatingRequest : OutboxServicesError()
    class CooldownNotExpired(val cooldown: Int) : OutboxServicesError()
}

private const val COOLDOWN_TIME = 500000

@Component
class OutboxServices(
    private val transactionManager: TransactionManager,
    private val emailService: EmailService
) {

    //TODO: Insert user,otp,timestamp,sent in entity + call create user
    //TODO: Send emails to users
    //TODO: Check if email was sent
    //TODO: Delete requests from outbox
    //TODO: If otp is expired, delete it - trigger
    //TODO: If email was not sent, sendEmails()
    //TODO: If otp is different, block for 5 minutes and then repeat process - cooldown table

    fun createUserVerification(userId:Int):OutboxResponse {
        val otp = createRamdomOtp()
        return transactionManager.run {
            val cooldown = it.cooldownRepository.getCooldownRequest(userId)
            if(cooldown != null) {
                return@run Either.Left(OutboxServicesError.CooldownNotExpired(cooldown))
            }
            val outbox = it.outboxRepository.createOutboxRequest(OutboxInput(userId, otp))
            if(outbox == null) {
                return@run Either.Left(OutboxServicesError.ErrorCreatingRequest)
            } else {
                return@run Either.Right(Unit)
            }
        }
    }

    fun checkOtp(userId:Int, otp:Int):OutboxResponse {
        return transactionManager.run {
            val outbox = it.outboxRepository.getOutboxRequestByUserId(userId)
            if(outbox == null) {
                Either.Left(OutboxServicesError.OtpNotFound)
            } else {
                if (outbox.expired_at.before(System.currentTimeMillis().toTimestamp())) {
                    it.outboxRepository.deleteOutboxRequest(outbox.id)
                    Either.Left(OutboxServicesError.OtpExpired)
                }
                if(outbox.otp == otp) {
                    it.usersRepository.updateStudentStatus(userId)
                    Either.Right(Unit)
                } else {
                    it.outboxRepository.deleteOutboxRequest(outbox.id)
                    it.cooldownRepository.createCooldownRequest(userId, addTime(COOLDOWN_TIME))
                    Either.Left(OutboxServicesError.OtpDifferent)
                }
            }
        }
    }

    @Scheduled(fixedRate = 10000)
    fun sendEmails() {
        transactionManager.run {
            it.outboxRepository.getOutboxPendingRequests().forEach { outbox ->
                it.usersRepository.getUserById(outbox.userId)?.let { user ->
                    emailService.sendVerificationEmail(user.name,user.email, outbox.otp)
                    it.outboxRepository.updateOutboxStateRequest(outbox.id)
                }
            }
        }
    }


    private fun createRamdomOtp():Int {
        return (100000..999999).random()
    }

    private fun Long.toTimestamp(): java.sql.Timestamp {
        return java.sql.Timestamp(this)
    }

    private fun java.sql.Timestamp.toLong(): Long {
        return this.time
    }

    private fun addTime(millis: Int): Timestamp {
        return (System.currentTimeMillis() + millis).toTimestamp()
    }

}

