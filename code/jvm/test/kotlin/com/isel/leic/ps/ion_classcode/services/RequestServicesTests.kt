package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.input.request.ApplyInput
import com.isel.leic.ps.ion_classcode.http.services.RequestServices
import com.isel.leic.ps.ion_classcode.http.services.RequestServicesError
import com.isel.leic.ps.ion_classcode.repository.request.ApplyRequestRepository
import com.isel.leic.ps.ion_classcode.repository.transaction.Transaction
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@SpringBootTest
class RequestServicesTests {
    @Autowired
    lateinit var requestServices: RequestServices

    @TestConfiguration
    class Config {
        @Bean
        fun transactionManager(): TransactionManager = object : TransactionManager {
            override fun <R> run(block: (Transaction) -> R): R {
                val mockedTransaction = mock<Transaction> {
                    val mockedApplyRequestRepository = mock<ApplyRequestRepository> {
                        on { createApplyRequest(request = ApplyInput(creator = 1)) } doReturn 1
                    }
                    on { applyRequestRepository } doReturn mockedApplyRequestRepository
                }
                return block(mockedTransaction)
            }
        }
    }

    @Test
    fun `createApplyRequest should give an InvalidData because the creator is invalid`() {
        // given: an invalid creator
        val creator = -1

        // when: getting an error because of an invalid creator
        val request = requestServices.createApplyRequest(
            applyInput = ApplyInput(creator = creator, composite = null),
        )

        if (request is Either.Left) {
            assert(request.value is RequestServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createApplyRequest should give an InvalidData because the composite is invalid`() {
        // given: an invalid creator
        val composite = -1

        // when: getting an error because of an invalid composite
        val request = requestServices.createApplyRequest(
            applyInput = ApplyInput(creator = 1, composite = composite),
        )

        if (request is Either.Left) {
            assert(request.value is RequestServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createApplyRequest should give the id of the request`() {
        // when: getting the id of the request
        val request = requestServices.createApplyRequest(
            applyInput = ApplyInput(creator = 1, composite = null),
        )

        if (request is Either.Right) {
            assert(request.value == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }
}
