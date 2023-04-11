package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.input.request.ApplyInput
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias CreateApplyRequestResult = Either<RequestServicesError, Int>

/**
 * Error codes for the services
 */
sealed class RequestServicesError

/**
 * Service to the request services
 */
@Component
class RequestServices(
    private val transactionManager: TransactionManager,
) {

    /**
     * Method to create a new apply to teacher request
     */
    fun createApplyRequest(applyInput: ApplyInput): CreateApplyRequestResult {
        return transactionManager.run {
            val request = it.applyRequestRepository.createApplyRequest(applyInput)
            Either.Right(request)
        }
    }
}
