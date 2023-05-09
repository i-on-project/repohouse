package com.isel.leic.ps.ionClassCode.http.controllers.web

import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.Teacher
import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.http.Uris
import com.isel.leic.ps.ionClassCode.http.model.input.AssignmentInputModel
import com.isel.leic.ps.ionClassCode.http.model.output.AssignmentCreatedOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.AssignmentDeletedOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.StudentAssignmentOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.TeacherAssignmentOutputModel
import com.isel.leic.ps.ionClassCode.http.model.problem.Problem
import com.isel.leic.ps.ionClassCode.infra.LinkRelation
import com.isel.leic.ps.ionClassCode.infra.siren
import com.isel.leic.ps.ionClassCode.services.AssignmentServices
import com.isel.leic.ps.ionClassCode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Assigment controller
 * All the write operations are only for teachers and need to ensure that the classroom is not archived
 */
@RestController
class AssigmentController(
    private val assigmentService: AssignmentServices,
) {
    /**
     * Get all information about an assigment
     */
    @GetMapping(Uris.ASSIGMENT_PATH)
    fun getAssigmentInfo(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
    ): ResponseEntity<*> {
        if (user is Student) {
            return getStudentAssigmentInfo(user, courseId, classroomId, assignmentId)
        }
        return getTeacherAssigmentInfo(courseId, classroomId, assignmentId)
    }

    /**
     * Get all information about an assigment for a student
     */
    private fun getStudentAssigmentInfo(user: User, courseId: Int, classroomId: Int, assignmentId: Int): ResponseEntity<*> {
        return when (val assignment = assigmentService.getStudentAssignmentInfo(assignmentId, user.id)) {
            is Result.Problem -> assigmentService.problem(assignment.value)
            is Result.Success -> siren(value = StudentAssignmentOutputModel(assignment.value.assignment, assignment.value.deliveries, assignment.value.team)) {
                clazz("assigment")
                link(rel = LinkRelation("self"), href = Uris.assigmentUri(courseId, classroomId, assignmentId), needAuthentication = true)
            }
        }
    }

    /**
     * Get all information about an assigment for a teacher
     */
    private fun getTeacherAssigmentInfo(courseId: Int, classroomId: Int, assignmentId: Int): ResponseEntity<*> {
        return when (val assignment = assigmentService.getTeacherAssignmentInfo(assignmentId)) {
            is Result.Problem -> assigmentService.problem(assignment.value)
            is Result.Success -> siren(value = TeacherAssignmentOutputModel(assignment.value.assignment, assignment.value.deliveries, assignment.value.teams)) {
                clazz("assigment")
                link(
                    rel = LinkRelation("self"),
                    href = Uris.assigmentUri(courseId, classroomId, assignmentId),
                    needAuthentication = true,
                )
            }
        }
    }

    /**
     * Get all information about an assigment
     */
    @PostMapping(Uris.CREATE_ASSIGNMENT_PATH)
    fun createAssignment(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @RequestBody assignmentInfo: AssignmentInputModel,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val assignment = assigmentService.createAssignment(assignmentInfo, user.id)) {
            is Result.Problem -> assigmentService.problem(assignment.value)
            is Result.Success -> siren(value = AssignmentCreatedOutputModel(assignment.value)) {
                clazz("assigment")
                link(rel = LinkRelation("self"), href = Uris.assigmentUri(courseId, classroomId, assignment.value.id), needAuthentication = true)
            }
        }
    }

    /**
     * Delete an assigment need to not have deliveries
     * Only Teacher
     */
    @DeleteMapping(Uris.DELETE_ASSIGMENT_PATH)
    fun deleteAssignment(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val delete = assigmentService.deleteAssignment(assignmentId)) {
            is Result.Problem -> assigmentService.problem(delete.value)
            is Result.Success -> siren(value = AssignmentDeletedOutputModel(delete.value)) {
                clazz("assigment-deleted")
                link(rel = LinkRelation("self"), href = Uris.assigmentUri(courseId, classroomId, assignmentId), needAuthentication = true)
            }
        }
    }
}
