package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.domain.input.request.ApplyInput
import com.isel.leic.ps.ion_classcode.http.controllers.AuthController
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubUserInfo
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiUsersRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.transaction.JdbiTransaction
import com.isel.leic.ps.ion_classcode.repository.jdbi.transaction.JdbiTransactionManager
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import java.text.FieldPosition
import org.springframework.stereotype.Component

typealias CreateApplyRequestResult = Either<RequestServicesError, Int>


sealed class RequestServicesError {

}

@Component
class RequestServices(
    private val transactionManager: TransactionManager,
) {

    fun createApplyRequest(applyInput: ApplyInput): CreateApplyRequestResult {
        return transactionManager.run {
            val request = it.applyRequestRepository.createApplyRequest(applyInput)
            Either.Right(request)
        }
    }

}
