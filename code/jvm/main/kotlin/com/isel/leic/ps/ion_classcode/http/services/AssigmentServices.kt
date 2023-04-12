package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Assignment
import com.isel.leic.ps.ion_classcode.domain.Team
import com.isel.leic.ps.ion_classcode.domain.input.AssignmentInput
import com.isel.leic.ps.ion_classcode.http.model.input.AssignmentInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.AssignmentModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias AssigmentResponse = Either<AssignmentServicesError, AssignmentModel>
typealias AssignmentCreatedResponse = Either<AssignmentServicesError, Assignment>
typealias AssignmentDeletedResponse = Either<AssignmentServicesError, Boolean>
typealias AssignmentStudentTeamResponse = Either<AssignmentServicesError, List<Team>>

sealed class AssignmentServicesError {
    object NotTeacher : AssignmentServicesError()
    object InvalidInput : AssignmentServicesError()
    object AssignmentNotFound : AssignmentServicesError()
    object AssignmentNotDeleted : AssignmentServicesError()
    object ClassroomArchived : AssignmentServicesError()
    object ClassroomNotFound : AssignmentServicesError()
}

@Component
class AssigmentServices(
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
