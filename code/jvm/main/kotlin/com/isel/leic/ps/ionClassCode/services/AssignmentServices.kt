package com.isel.leic.ps.ionClassCode.services

import com.isel.leic.ps.ionClassCode.domain.Assignment
import com.isel.leic.ps.ionClassCode.domain.Team
import com.isel.leic.ps.ionClassCode.domain.input.AssignmentInput
import com.isel.leic.ps.ionClassCode.http.model.input.AssignmentInputModel
import com.isel.leic.ps.ionClassCode.http.model.output.StudentAssignmentModel
import com.isel.leic.ps.ionClassCode.http.model.output.TeacherAssignmentModel
import com.isel.leic.ps.ionClassCode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ionClassCode.http.model.problem.Problem
import com.isel.leic.ps.ionClassCode.repository.transaction.TransactionManager
import com.isel.leic.ps.ionClassCode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias StudentAssignmentResponse = Result<AssignmentServicesError, StudentAssignmentModel>
typealias TeacherAssignmentResponse = Result<AssignmentServicesError, TeacherAssignmentModel>
typealias AssignmentCreatedResponse = Result<AssignmentServicesError, Assignment>
typealias AssignmentDeletedResponse = Result<AssignmentServicesError, Boolean>
typealias AssignmentStudentTeamResponse = Result<AssignmentServicesError, List<Team>>

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
    object InternalError : AssignmentServicesError()
}

/**
 * Services for the assigment
 */
@Component
class AssignmentServices(
    val transactionManager: TransactionManager,
) {

    fun createAssignment(assignmentInfo: AssignmentInputModel, userId: Int): AssignmentCreatedResponse {
        if (assignmentInfo.isNotValid()) return Result.Problem(AssignmentServicesError.InvalidInput)
        return transactionManager.run {
            if (it.usersRepository.getTeacher(userId) == null) return@run Result.Problem(AssignmentServicesError.InternalError)
            val classroom = it.classroomRepository.getClassroomById(assignmentInfo.classroomId)

            if (classroom == null) {
                return@run Result.Problem(AssignmentServicesError.ClassroomNotFound)
            } else if (classroom.isArchived) {
                return@run Result.Problem(AssignmentServicesError.ClassroomArchived)
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
            Result.Success(assignment)
        }
    }

    /**
     * Method that gets an assigment for a teacher
     */
    fun getTeacherAssignmentInfo(assignmentId: Int): TeacherAssignmentResponse {
        return transactionManager.run {
            val assignment = it.assignmentRepository.getAssignmentById(assignmentId)
            if (assignment == null) {
                Result.Problem(AssignmentServicesError.AssignmentNotFound)
            } else {
                val deliveries = it.deliveryRepository.getDeliveriesByAssignment(assignmentId)
                val teams = it.teamRepository.getTeamsFromAssignment(assignmentId)
                Result.Success(TeacherAssignmentModel(assignment, deliveries, teams))
            }
        }
    }

    /**
     * Method that gets an assigment for a student
     */
    fun getStudentAssignmentInfo(assignmentId: Int, studentId: Int): StudentAssignmentResponse {
        return transactionManager.run {
            it.usersRepository.getStudent(studentId) ?: return@run Result.Problem(AssignmentServicesError.InternalError)
            val assignment = it.assignmentRepository.getAssignmentById(assignmentId = assignmentId)
            if (assignment == null) {
                Result.Problem(value = AssignmentServicesError.AssignmentNotFound)
            } else {
                val deliveries = it.deliveryRepository.getDeliveriesByAssignment(assignmentId = assignmentId)
                val team = it.teamRepository.getTeamsFromStudent(studentId = studentId).find { team ->
                    team.assignment == assignmentId
                }
                Result.Success(value = StudentAssignmentModel(assignment = assignment, deliveries = deliveries, team = team))
            }
        }
    }

    /**
     * Method that deletes an assigment
     * Checks if the classroom is not archived
     */
    fun deleteAssignment(assignmentId: Int): AssignmentDeletedResponse {
        return transactionManager.run {
            val assignment = it.assignmentRepository.getAssignmentById(assignmentId = assignmentId)
            if (assignment == null) {
                return@run Result.Problem(value = AssignmentServicesError.AssignmentNotFound)
            } else {
                val classroom = it.classroomRepository.getClassroomById(classroomId = assignment.classroomId)

                if (classroom == null) {
                    return@run Result.Problem(value = AssignmentServicesError.ClassroomNotFound)
                } else if (classroom.isArchived) {
                    return@run Result.Problem(value = AssignmentServicesError.ClassroomArchived)
                }

                if (it.deliveryRepository.getDeliveriesByAssignment(assignmentId = assignmentId).isNotEmpty()) {
                    return@run Result.Problem(
                        value = AssignmentServicesError.AssignmentNotDeleted,
                    )
                }
                it.assignmentRepository.deleteAssignment(assignmentId = assignmentId)
                Result.Success(true)
            }
        }
    }

    /**
     * Method that gets the teams from a student of an assigment
     */
    fun getAssignmentStudentTeams(assignmentId: Int, studentId: Int): AssignmentStudentTeamResponse {
        return transactionManager.run {
            it.usersRepository.getStudent(studentId) ?: return@run Result.Problem(AssignmentServicesError.InternalError)
            val assignment = it.assignmentRepository.getAssignmentById(assignmentId = assignmentId)
            if (assignment == null) {
                Result.Problem(value = AssignmentServicesError.AssignmentNotFound)
            } else {
                val assigmentTeams = it.teamRepository.getTeamsFromAssignment(assignmentId = assignmentId)
                val studentTeams = it.teamRepository.getTeamsFromStudent(studentId = studentId)
                Result.Success(value = assigmentTeams.filter { assigmentTeam -> studentTeams.any { studentTeam -> studentTeam.id == assigmentTeam.id } })
            }
        }
    }

    /**
     * Function to handle errors
     */
    fun problem(error: AssignmentServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            AssignmentServicesError.NotTeacher -> Problem.notTeacher
            AssignmentServicesError.InvalidInput -> Problem.invalidInput
            AssignmentServicesError.AssignmentNotFound -> Problem.notFound
            AssignmentServicesError.AssignmentNotDeleted -> Problem.methodNotAllowed
            AssignmentServicesError.ClassroomArchived -> Problem.invalidOperation
            AssignmentServicesError.InternalError -> Problem.internalError
            AssignmentServicesError.ClassroomNotFound -> Problem.notFound
        }
    }
}
