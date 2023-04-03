package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.input.AssignmentInput
import com.isel.leic.ps.ion_classcode.http.model.input.AssigmentInputModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias AssigmentCreatedResponse = Either<AssigmentServicesError, Assigment>

sealed class AssigmentServicesError {
    object NotTeacher : AssigmentServicesError()
    object InvalidInput : AssigmentServicesError()
}

@Component
class AssigmentServices(
    val transactionManager: TransactionManager
) {

    fun createAssigment(assigmentInfo: AssigmentInputModel, userId: Int): AssigmentCreatedResponse {
        if (
            assigmentInfo.classroomId > 0 ||
            assigmentInfo.maxNumberElems > 0 ||
            assigmentInfo.maxNumberGroups > 0 ||
            assigmentInfo.description.isNotBlank() ||
            assigmentInfo.title.isNotBlank()
        ) return Either.Left(AssigmentServicesError.InvalidInput)
        return transactionManager.run {
            if (it.usersRepository.getTeacher(userId) == null) Either.Left(AssigmentServicesError.NotTeacher)
            val assigment = it.assigmentRepository.createAssignment(AssignmentInput(
                assigmentInfo.classroomId,
                assigmentInfo.maxNumberElems,
                assigmentInfo.maxNumberGroups,
                assigmentInfo.description,
                assigmentInfo.title
            ))
            Either.Right(assigment)
        }
    }
}
