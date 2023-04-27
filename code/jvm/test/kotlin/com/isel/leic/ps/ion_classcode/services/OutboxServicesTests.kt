package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.Outbox
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.input.OutboxInput
import com.isel.leic.ps.ion_classcode.repository.CooldownRepository
import com.isel.leic.ps.ion_classcode.repository.OutboxRepository
import com.isel.leic.ps.ion_classcode.repository.UsersRepository
import com.isel.leic.ps.ion_classcode.repository.transaction.Transaction
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Result
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.sql.Timestamp
import java.time.Instant

@SpringBootTest
class OutboxServicesTests {

    @Autowired
    lateinit var outboxServices: OutboxServices

    @TestConfiguration
    class Config {
        @Bean
        fun transactionManager(): TransactionManager = object : TransactionManager {
            override fun <R> run(block: (Transaction) -> R): R {
                val mockedTransaction = mock<Transaction> {
                    val mockedOutboxRepository = mock<OutboxRepository> {
                        on { createOutboxRequest(outbox = OutboxInput(userId = 1, otp = 3)) } doReturn 1
                        on { getOutboxRequest(userId = 1) } doReturn Outbox(userId = 1, otp = 1, status = "Status", expiredAt = Timestamp.from(Instant.from(Instant.now().minusSeconds(1000))))
                        on { getOutboxRequest(userId = 2) } doReturn Outbox(userId = 2, otp = 2, status = "Status", expiredAt = Timestamp.from(Instant.from(Instant.now().plusSeconds(1000))))
                    }
                    val mockedUsersRepository = mock<UsersRepository> {
                        on { getUserById(userId = 1) } doReturn Student(name = "test1245", email = "test@alunos.isel.pt", githubUsername = "test1a23", token = "token5", githubId = 124345, isCreated = false, id = 1, schoolId = null)
                    }
                    val mockedCooldownRepository = mock<CooldownRepository> {
                        on { getCooldownRequest(userId = 1) } doReturn 1
                        on { createCooldownRequest(userId = 2, endTime = Timestamp(System.currentTimeMillis() + 500000)) } doReturn 1
                    }
                    on { outboxRepository } doReturn mockedOutboxRepository
                    on { usersRepository } doReturn mockedUsersRepository
                    on { cooldownRepository } doReturn mockedCooldownRepository
                }
                return block(mockedTransaction)
            }
        }
    }

    // TEST: createUserVerification

    @Test
    fun `createUserVerification should give an InvalidInput because the userId is invalid`() {
        // given: an invalid user id
        val userId = -1

        // when: getting an error because of an invalid user id
        val outboxOtp = outboxServices.createUserVerification(
            userId = userId,
        )

        if (outboxOtp is Result.Problem) {
            assert(outboxOtp.value is OutboxServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getCooldownRequest should give an CooldownNotExpired because the userId is invalid`() {
        // given: an invalid user id
        val userId = 3

        // when: getting an error because of an invalid user id
        val outboxOtp = outboxServices.createUserVerification(
            userId = userId,
        )

        if (outboxOtp is Result.Problem) {
            assert(outboxOtp.value is OutboxServicesError.CooldownNotExpired)
        } else {
            fail("Should not be Either.Right")
        }
    }

    // TEST: checkOtp

    @Test
    fun `checkOtp should give an InvalidInput because the userId is invalid`() {
        // given: an invalid user id
        val userId = -1

        // when: getting an error because of an invalid user id
        val outboxOtp = outboxServices.checkOtp(
            userId = userId,
            otp = 1,
        )

        if (outboxOtp is Result.Problem) {
            assert(outboxOtp.value is OutboxServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `checkOtp should give an OtpNotFound because the otp is not in database`() {
        // given: an invalid otp
        val userId = 3

        // when: getting an error because the otp is not in database
        val outboxOtp = outboxServices.checkOtp(
            userId = userId,
            otp = 1,
        )

        if (outboxOtp is Result.Problem) {
            assert(outboxOtp.value is OutboxServicesError.OtpNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `checkOtp should give an OtpExpired because the otp is expired`() {
        // given: an invalid otp
        val otp = 2

        // when: getting an error because the otp  is expired
        val outboxOtp = outboxServices.checkOtp(
            userId = 1,
            otp = otp,
        )

        if (outboxOtp is Result.Problem) {
            assert(outboxOtp.value is OutboxServicesError.OtpExpired)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `checkOtp should give an OtpDifferent because the otp is expired`() {
        // given: an valid otp and user id
        val userId = 2
        val otp = 1

        // when: getting an error because the otp is wrong
        val outboxOtp = outboxServices.checkOtp(
            userId = userId,
            otp = otp,
        )

        if (outboxOtp is Result.Problem) {
            assert(outboxOtp.value is OutboxServicesError.OtpDifferent)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `checkOtp should give unit`() {
        // given: an valid otp and user id
        val userId = 2
        val otp = 2

        // when: getting a unit
        val outboxOtp = outboxServices.checkOtp(
            userId = userId,
            otp = otp,
        )

        if (outboxOtp is Result.Success) {
            assert(outboxOtp.value == Unit)
        } else {
            fail("Should not be Either.Left")
        }
    }
}
