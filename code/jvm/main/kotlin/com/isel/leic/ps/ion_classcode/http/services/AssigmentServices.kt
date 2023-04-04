package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.Team
import com.isel.leic.ps.ion_classcode.domain.input.AssignmentInput
import com.isel.leic.ps.ion_classcode.http.model.input.AssigmentInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.AssigmentOutputModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias AssigmentResponse = Either<AssigmentServicesError, AssigmentOutputModel>
typealias AssigmentCreatedResponse = Either<AssigmentServicesError, Assigment>
typealias AssigmentDeletedResponse = Either<AssigmentServicesError, Boolean>
typealias AssigmentStudentTeamResponse = Either<AssigmentServicesError, List<Team>>

sealed class AssigmentServicesError {
    object NotTeacher : AssigmentServicesError()
    object InvalidInput : AssigmentServicesError()
    object AssigmentNotFound : AssigmentServicesError()
    object AssigmentNotDeleted : AssigmentServicesError()
    object ClassroomArchived : AssigmentServicesError()
    object ClassroomNotFound : AssigmentServicesError()
}

@Component
class AssigmentServices(
    val transactionManager: TransactionManager,
) {

    fun createAssigment(assigmentInfo: AssigmentInputModel, userId: Int): AssigmentCreatedResponse {
        if (
            assigmentInfo.classroomId > 0 &&
            assigmentInfo.maxNumberElems > 0 &&
            assigmentInfo.maxNumberGroups > 0 &&
            assigmentInfo.description.isNotBlank() &&
            assigmentInfo.title.isNotBlank()
        ) {
            return Either.Left(AssigmentServicesError.InvalidInput)
        }
        return transactionManager.run {
            if (it.usersRepository.getTeacher(userId) == null) Either.Left(AssigmentServicesError.NotTeacher)
            val classroom = it.classroomRepository.getClassroomById(assigmentInfo.classroomId)

            if (classroom == null) {
                Either.Left(AssigmentServicesError.ClassroomNotFound)
            } else if (classroom.isArchived) {
                Either.Left(AssigmentServicesError.ClassroomArchived)
            }
            val assigment = it.assigmentRepository.createAssignment(
                AssignmentInput(
                    assigmentInfo.classroomId,
                    assigmentInfo.maxNumberElems,
                    assigmentInfo.maxNumberGroups,
                    assigmentInfo.description,
                    assigmentInfo.title,
                ),
            )
            Either.Right(assigment)
        }
    }

    fun getAssigmentInfo(assigmentId: Int): AssigmentResponse {
        return transactionManager.run {
            val assigment = it.assigmentRepository.getAssignmentById(assigmentId)
            if (assigment == null) {
                Either.Left(AssigmentServicesError.AssigmentNotFound)
            } else {
                val deliveries = it.deliveryRepository.getDeliveriesByAssigment(assigmentId)
                val teams = it.teamRepository.getTeamsFromAssignment(assigmentId)
                Either.Right(AssigmentOutputModel(assigment, deliveries, teams))
            }
        }
    }

    fun deleteAssigment(assigmentId: Int): AssigmentDeletedResponse {
        return transactionManager.run {
            val assigment = it.assigmentRepository.getAssignmentById(assigmentId)
            if (assigment == null) {
                Either.Left(AssigmentServicesError.AssigmentNotFound)
            } else {
                val classroom = it.classroomRepository.getClassroomById(assigment.classroomId)

                if (classroom == null) {
                    Either.Left(AssigmentServicesError.ClassroomNotFound)
                } else if (classroom.isArchived) {
                    Either.Left(AssigmentServicesError.ClassroomArchived)
                }

                if (it.deliveryRepository.getDeliveriesByAssigment(assigmentId).isNotEmpty()) {
                    Either.Left(
                        AssigmentServicesError.AssigmentNotDeleted,
                    )
                }
                it.assigmentRepository.deleteAssignment(assigmentId)
                Either.Right(true)
            }
        }
    }

    fun getAssigmentStudentTeams(assigmentId: Int, studentId: Int): AssigmentStudentTeamResponse {
        return transactionManager.run {
            val assigment = it.assigmentRepository.getAssignmentById(assigmentId)
            if (assigment == null) {
                Either.Left(AssigmentServicesError.AssigmentNotFound)
            } else {
                val assigmentTeams = it.teamRepository.getTeamsFromAssignment(assigmentId)
                val studentTeams = it.teamRepository.getTeamsFromStudent(studentId)
                Either.Right(assigmentTeams.filter { assigmentTeam -> studentTeams.any { studentTeam -> studentTeam.id == assigmentTeam.id } })
            }
        }
    }
}
