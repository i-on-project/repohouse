package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Assignment
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
typealias AssignmentResponse = Either<AssigmentServicesError, AssigmentModel>
typealias AssignmentCreatedResponse = Either<AssigmentServicesError, Assigment>
typealias AssignmentDeletedResponse = Either<AssigmentServicesError, Boolean>
typealias AssignmentStudentTeamResponse = Either<AssigmentServicesError, List<Team>>

/**
 * Error codes for the services
 */
sealed class AssignmentServicesError {
    object NotTeacher : AssignmentServicesError()
    object InvalidInput : AssignmentServicesError()
    object AssignmentNotFound : AssignmentServicesError()
    object AssignmentNotDeleted : AssignmentServicesError()
    object ClassroomArchived : AssignmentServicesError()
    object ClassroomNotFound : AssignmentServicesError()
}

/**
 * Services for the assigment
 */
@Component
class AssignmentServices(
    val transactionManager: TransactionManager,
) {

    fun createAssignment(assignmentInfo: AssignmentInputModel, userId: Int): AssignmentCreatedResponse {
        if (assignmentInfo.isNotValid() || userId <= 0) {
            return Either.Left(value = AssignmentServicesError.InvalidInput)
        }
        return transactionManager.run {
            if (it.usersRepository.getTeacher(teacherId = userId) == null) return@run Either.Left(value = AssignmentServicesError.NotTeacher)
            val classroom = it.classroomRepository.getClassroomById(classroomId = assignmentInfo.classroomId)

            if (classroom == null) {
                return@run Either.Left(value = AssignmentServicesError.ClassroomNotFound)
            } else if (classroom.isArchived) {
                return@run Either.Left(value = AssignmentServicesError.ClassroomArchived)
            }
            val assignment = it.assignmentRepository.createAssignment(
                AssignmentInput(
                    classroomId = assignmentInfo.classroomId,
                    maxElemsPerGroup = assignmentInfo.maxNumberElems,
                    maxNumberGroups = assignmentInfo.maxNumberGroups,
                    description = assignmentInfo.description,
                    title = assignmentInfo.title,
                ),
            )
            Either.Right(value = assignment)
        }
    }

    /**
     * Method that gets an assigment
     */
    fun getAssigmentInfo(assignmentId: Int): AssigmentResponse {
        if (assignmentId <= 0) return Either.Left(value = AssignmentServicesError.InvalidInput)
        return transactionManager.run {
            val assignment = it.assignmentRepository.getAssignmentById(assignmentId = assignmentId)
            if (assignment == null) {
                Either.Left(value = AssignmentServicesError.AssignmentNotFound)
            } else {
                val deliveries = it.deliveryRepository.getDeliveriesByAssignment(assignmentId = assignmentId)
                val teams = it.teamRepository.getTeamsFromAssignment(assignmentId = assignmentId)
                Either.Right(value = AssignmentModel(assignment = assignment, deliveries = deliveries, teams = teams))
            }
        }
    }

    /**
     * Method that deletes an assigment
     * Checks if the classroom is not archived
     */
    fun deleteAssignment(assignmentId: Int): AssignmentDeletedResponse {
        if (assignmentId <= 0) return Either.Left(value = AssignmentServicesError.InvalidInput)
        return transactionManager.run {
            val assignment = it.assignmentRepository.getAssignmentById(assignmentId = assignmentId)
            if (assignment == null) {
                return@run Either.Left(value = AssignmentServicesError.AssignmentNotFound)
            } else {
                val classroom = it.classroomRepository.getClassroomById(classroomId = assignment.classroomId)

                if (classroom == null) {
                    return@run Either.Left(value = AssignmentServicesError.ClassroomNotFound)
                } else if (classroom.isArchived) {
                    return@run Either.Left(value = AssignmentServicesError.ClassroomArchived)
                }

                if (it.deliveryRepository.getDeliveriesByAssignment(assignmentId = assignmentId).isNotEmpty()) {
                    return@run Either.Left(
                        value = AssignmentServicesError.AssignmentNotDeleted,
                    )
                }
                it.assignmentRepository.deleteAssignment(assignmentId = assignmentId)
                Either.Right(true)
            }
        }
    }

    /**
     * Method that gets the teams from a student of an assigment
     */
    fun getAssignmentStudentTeams(assignmentId: Int, studentId: Int): AssignmentStudentTeamResponse {
        if (assignmentId <= 0 || studentId <= 0) return Either.Left(value = AssignmentServicesError.InvalidInput)
        return transactionManager.run {
            val assignment = it.assignmentRepository.getAssignmentById(assignmentId = assignmentId)
            if (assignment == null) {
                Either.Left(value = AssignmentServicesError.AssignmentNotFound)
            } else {
                val assigmentTeams = it.teamRepository.getTeamsFromAssignment(assignmentId = assignmentId)
                val studentTeams = it.teamRepository.getTeamsFromStudent(studentId = studentId)
                Either.Right(value = assigmentTeams.filter { assigmentTeam -> studentTeams.any { studentTeam -> studentTeam.id == assigmentTeam.id } })
            }
        }
    }
}
