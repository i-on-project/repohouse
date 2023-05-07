package com.isel.leic.ps.ion_classcode.http.controllers.mobile

import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.output.TeacherAssignmentModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeacherAssignmentOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.services.AssignmentServices
import com.isel.leic.ps.ion_classcode.services.AssignmentServicesError
import com.isel.leic.ps.ion_classcode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class AssigmentControllerMobile(
    private val assigmentService: AssignmentServices,
) {
    @GetMapping(Uris.MOBILE_ASSIGMENT_PATH, produces = ["application/vnd.siren+json"])
    fun getAssigmentInfo(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
    ): ResponseEntity<*> {
        return when (val assignment = assigmentService.getTeacherAssignmentInfo(assignmentId = assignmentId)) {
            is Result.Problem -> problem(error = assignment.value)
            is Result.Success -> {
                assignment.value as TeacherAssignmentModel
                siren(
                    value = TeacherAssignmentOutputModel(
                        assignment = assignment.value.assignment,
                        deliveries = assignment.value.deliveries,
                        teams = assignment.value.teams,
                    ),
                ) {
                    clazz("assignment")
                    link(
                        rel = LinkRelation("self"),
                        href = Uris.assigmentUri(courseId = courseId, classroomId = classroomId, assignmentId = assignmentId),
                        needAuthentication = true,
                    )
                    link(rel = LinkRelation("course"), href = Uris.courseUri(courseId = courseId), needAuthentication = true)
                    link(
                        rel = LinkRelation("classroom"),
                        href = Uris.classroomUri(courseId = courseId, classroomId = classroomId),
                        needAuthentication = true,
                    )
                    link(
                        rel = LinkRelation("assigments"),
                        href = Uris.assignmentsUri(courseId = courseId, classroomId = classroomId),
                        needAuthentication = true,
                    )
                    assignment.value.teams.forEach {
                        link(
                            rel = LinkRelation("team"),
                            href = Uris.teamUri(courseId = courseId, classroomId = classroomId, assignmentId = assignmentId, teamId = it.id),
                            needAuthentication = true,
                        )
                    }
                }
            }
        }
    }

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
