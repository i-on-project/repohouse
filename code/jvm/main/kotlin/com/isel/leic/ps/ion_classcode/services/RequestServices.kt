package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.input.ApplyInput
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Result
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias CreateApplyRequestResult = Result<RequestServicesError, Int>

/**
 * Error codes for the services
 */
sealed class RequestServicesError {
    object InvalidData : RequestServicesError()
}

/**
 * Service to the request services
 */
@Component
class RequestServices(
    private val transactionManager: TransactionManager,
) {

    /**
     * Method to create a new applying to teacher request
     */
    fun createApplyRequest(applyInput: ApplyInput, creator:Int): CreateApplyRequestResult {
        if (applyInput.isNotValid()) {
            return Result.Problem(value = RequestServicesError.InvalidData)
        }
        return transactionManager.run {
            val request = it.applyRequestRepository.createApplyRequest(request = applyInput)
            Result.Success(value = request)
        }
    }
}
