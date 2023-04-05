package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.input.request.ApplyInputInterface
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias CreateApplyRequestResult = Either<RequestServicesError, Int>

sealed class RequestServicesError

@Component
class RequestServices(
    private val transactionManager: TransactionManager,
) {

    fun createApplyRequest(applyInput: ApplyInputInterface): CreateApplyRequestResult {
        return transactionManager.run {
            val request = it.applyRequestRepository.createApplyRequest(applyInput)
            Either.Right(request)
        }
    }
}
