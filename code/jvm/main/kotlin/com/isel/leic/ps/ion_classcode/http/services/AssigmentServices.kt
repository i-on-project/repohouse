package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.Team
import com.isel.leic.ps.ion_classcode.domain.input.AssignmentInput
import com.isel.leic.ps.ion_classcode.http.model.input.AssigmentInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.AssigmentModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias AssigmentResponse = Either<AssigmentServicesError, AssigmentModel>
typealias AssigmentCreatedResponse = Either<AssigmentServicesError, Assigment>
typealias AssigmentDeletedResponse = Either<AssigmentServicesError, Boolean>
typealias AssigmentStudentTeamResponse = Either<AssigmentServicesError, List<Team>>

/**
 * Error codes for the services
 */
sealed class AssigmentServicesError {
    object NotTeacher : AssigmentServicesError()
    object InvalidInput : AssigmentServicesError()
    object AssigmentNotFound : AssigmentServicesError()
    object AssigmentNotDeleted : AssigmentServicesError()
    object ClassroomArchived : AssigmentServicesError()
    object ClassroomNotFound : AssigmentServicesError()
}

/**
 * Services for the assigment
 */
@Component
class AssigmentServices(
    val transactionManager: TransactionManager,
) {

    /**
     * Method that creates an assigment
     */
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
            val assigment = it.assignmentRepository.createAssignment(
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

    /**
     * Method that gets an assigment
     */
    fun getAssigmentInfo(assigmentId: Int): AssigmentResponse {
        return transactionManager.run {
            val assigment = it.assignmentRepository.getAssignmentById(assigmentId)
            if (assigment == null) {
                Either.Left(AssigmentServicesError.AssigmentNotFound)
            } else {
                val deliveries = it.deliveryRepository.getDeliveriesByAssignment(assigmentId)
                val teams = it.teamRepository.getTeamsFromAssignment(assigmentId)
                Either.Right(AssigmentModel(assigment, deliveries, teams))
            }
        }
    }

    /**
     * Method that deletes an assigment
     * Checks if the classroom is not archived
     */
    fun deleteAssigment(assigmentId: Int): AssigmentDeletedResponse {
        return transactionManager.run {
            val assigment = it.assignmentRepository.getAssignmentById(assigmentId)
            if (assigment == null) {
                Either.Left(AssigmentServicesError.AssigmentNotFound)
            } else {
                val classroom = it.classroomRepository.getClassroomById(assigment.classroomId)

                if (classroom == null) {
                    Either.Left(AssigmentServicesError.ClassroomNotFound)
                } else if (classroom.isArchived) {
                    Either.Left(AssigmentServicesError.ClassroomArchived)
                }

                if (it.deliveryRepository.getDeliveriesByAssignment(assigmentId).isNotEmpty()) {
                    Either.Left(
                        AssigmentServicesError.AssigmentNotDeleted,
                    )
                }
                it.assignmentRepository.deleteAssignment(assigmentId)
                Either.Right(true)
            }
        }
    }

    /**
     * Method that gets the teams from a student of an assigment
     */
    fun getAssigmentStudentTeams(assigmentId: Int, studentId: Int): AssigmentStudentTeamResponse {
        return transactionManager.run {
            val assigment = it.assignmentRepository.getAssignmentById(assigmentId)
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
