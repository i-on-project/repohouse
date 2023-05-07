package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.input.AssignmentInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.AssignmentCreatedOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.AssignmentDeletedOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.StudentAssignmentModel
import com.isel.leic.ps.ion_classcode.http.model.output.StudentAssignmentOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeacherAssignmentModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeacherAssignmentOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.services.AssignmentServices
import com.isel.leic.ps.ion_classcode.services.AssignmentServicesError
import com.isel.leic.ps.ion_classcode.utils.Result
import org.springframework.http.HttpMethod
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
        return if (user is Student) {
            getStudentAssigmentInfo(user, courseId, classroomId, assignmentId)
        } else {
            getTeacherAssigmentInfo(user, courseId, classroomId, assignmentId)
        }
    }

    /**
     * Get all information about an assigment for a student
     */
    private fun getStudentAssigmentInfo(user: User, courseId: Int, classroomId: Int, assignmentId: Int): ResponseEntity<*> {
        return when (val assignment = assigmentService.getStudentAssignmentInfo(assignmentId, user.id)) {
            is Result.Problem -> problem(assignment.value)
            is Result.Success ->  {
                assignment.value as StudentAssignmentModel
                siren(value = StudentAssignmentOutputModel(assignment.value.assignment, assignment.value.deliveries, assignment.value.team)) {
                    clazz("assignment")
                    link(
                        rel = LinkRelation("self"),
                        href = Uris.assigmentUri(courseId, classroomId, assignmentId),
                        needAuthentication = true
                    )
                    // TODO: Check under this line
                    if (assignment.value.team == null) {
                        when (val studentTeams = assigmentService.getAssignmentStudentTeams(assignmentId, user.id)) {
                            is Result.Problem -> problem(studentTeams.value)
                            is Result.Success -> {
                                studentTeams.value.forEach {
                                    action(
                                        "join-team",
                                        Uris.joinTeamUri(courseId, classroomId, assignmentId, it.id),
                                        method = HttpMethod.POST,
                                        type = "application/json",
                                    ) {
                                        hiddenField("assigmentId", assignmentId.toString())
                                        numberField("teamId", it.id)
                                    }
                                }

                                if (studentTeams.value.size < assignment.value.assignment.maxNumberGroups) {
                                    action(
                                        "create-team",
                                        Uris.createTeamUri(courseId, classroomId, assignmentId),
                                        method = HttpMethod.POST,
                                        type = "application/json",
                                    ) {
                                        hiddenField("assigmentId", assignmentId.toString())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Get all information about an assigment for a teacher
     */
    private fun getTeacherAssigmentInfo(user: User, courseId: Int, classroomId: Int, assignmentId: Int): ResponseEntity<*> {
        return when (val assignment = assigmentService.getTeacherAssignmentInfo(assignmentId)) {
            is Result.Problem -> problem(assignment.value)
            is Result.Success -> {
                assignment.value as TeacherAssignmentModel
                siren(value = TeacherAssignmentOutputModel(assignment.value.assignment, assignment.value.deliveries, assignment.value.teams)) {
                    clazz("assignment")
                    link(
                        rel = LinkRelation("self"),
                        href = Uris.assigmentUri(courseId, classroomId, assignmentId),
                        needAuthentication = true,
                    )
                    link(rel = LinkRelation("course"), href = Uris.courseUri(courseId), needAuthentication = true)
                    link(
                        rel = LinkRelation("classroom"),
                        href = Uris.classroomUri(courseId, classroomId),
                        needAuthentication = true,
                    )
                    link(
                        rel = LinkRelation("assigments"),
                        href = Uris.assignmentsUri(courseId, classroomId),
                        needAuthentication = true,
                    )
                    assignment.value.deliveries.forEach {
                        link(
                            rel = LinkRelation("delivery"),
                            href = Uris.deliveryUri(courseId, classroomId, assignmentId, it.id),
                            needAuthentication = true,
                        )
                    }
                    assignment.value.teams.forEach {
                        link(
                            rel = LinkRelation("team"),
                            href = Uris.teamUri(courseId, classroomId, assignmentId, it.id),
                            needAuthentication = true,
                        )
                    }
                    action(
                        "create-delivery",
                        Uris.createDeliveryUri(courseId, classroomId, assignmentId),
                        method = HttpMethod.POST,
                        type = "application/json",
                    ) {
                        hiddenField("assigmentId", assignmentId.toString())
                        textField("tag-control")
                        timestampField("due-date")
                    }
                }
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
            is Result.Problem -> problem(assignment.value)
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
            is Result.Problem -> problem(delete.value)
            is Result.Success -> siren(value = AssignmentDeletedOutputModel(delete.value)) {
                clazz("assigment-deleted")
                link(rel = LinkRelation("self"), href = Uris.assigmentUri(courseId, classroomId, assignmentId), needAuthentication = true)
            }
        }
    }

    /**
     * Function to handle errors
     */
    private fun problem(error: AssignmentServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            AssignmentServicesError.NotTeacher -> Problem.notTeacher
            AssignmentServicesError.InvalidInput -> Problem.invalidInput
            AssignmentServicesError.AssignmentNotFound -> Problem.notFound
            AssignmentServicesError.AssignmentNotDeleted -> Problem.methodNotAllowed
            AssignmentServicesError.ClassroomArchived -> Problem.invalidOperation
            AssignmentServicesError.ClassroomNotFound -> Problem.notFound
        }
    }
}
