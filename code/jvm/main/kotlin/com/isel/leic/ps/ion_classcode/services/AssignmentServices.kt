package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.Assignment
import com.isel.leic.ps.ion_classcode.domain.Team
import com.isel.leic.ps.ion_classcode.domain.input.AssignmentInput
import com.isel.leic.ps.ion_classcode.http.model.input.AssignmentInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.AssignmentModel
import com.isel.leic.ps.ion_classcode.http.model.output.StudentAssignmentModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeacherAssignmentModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Result
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias AssignmentResponse = Result<AssignmentServicesError, AssignmentModel>
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
            return Result.Problem(value = AssignmentServicesError.InvalidInput)
        }
        return transactionManager.run {
            if (it.usersRepository.getTeacher(teacherId = userId) == null) return@run Result.Problem(value = AssignmentServicesError.NotTeacher)
            val classroom = it.classroomRepository.getClassroomById(classroomId = assignmentInfo.classroomId)

            if (classroom == null) {
                return@run Result.Problem(value = AssignmentServicesError.ClassroomNotFound)
            } else if (classroom.isArchived) {
                return@run Result.Problem(value = AssignmentServicesError.ClassroomArchived)
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
            Result.Success(value = assignment)
        }
    }

    /**
     * Method that gets an assigment for a teacher
     */
    fun getTeacherAssignmentInfo(assignmentId: Int): AssignmentResponse {
        if (assignmentId <= 0) return Result.Problem(value = AssignmentServicesError.InvalidInput)
        return transactionManager.run {
            val assignment = it.assignmentRepository.getAssignmentById(assignmentId = assignmentId)
            if (assignment == null) {
                Result.Problem(value = AssignmentServicesError.AssignmentNotFound)
            } else {
                val deliveries = it.deliveryRepository.getDeliveriesByAssignment(assignmentId = assignmentId)
                val teams = it.teamRepository.getTeamsFromAssignment(assignmentId = assignmentId)
                Result.Success(value = TeacherAssignmentModel(assignment = assignment, deliveries = deliveries, teams = teams))
            }
        }
    }

    /**
     * Method that gets an assigment for a student
     */
    fun getStudentAssignmentInfo(assignmentId: Int,studentId: Int): AssignmentResponse {
        if (assignmentId <= 0) return Result.Problem(value = AssignmentServicesError.InvalidInput)
        return transactionManager.run {
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
        if (assignmentId <= 0) return Result.Problem(value = AssignmentServicesError.InvalidInput)
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
        if (assignmentId <= 0 || studentId <= 0) return Result.Problem(value = AssignmentServicesError.InvalidInput)
        return transactionManager.run {
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
}
