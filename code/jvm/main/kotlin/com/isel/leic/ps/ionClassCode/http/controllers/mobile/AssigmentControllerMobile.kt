package com.isel.leic.ps.ionClassCode.http.controllers.mobile

import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.http.Uris
import com.isel.leic.ps.ionClassCode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ionClassCode.http.model.problem.Problem
import com.isel.leic.ps.ionClassCode.infra.LinkRelation
import com.isel.leic.ps.ionClassCode.infra.siren
import com.isel.leic.ps.ionClassCode.services.AssignmentServices
import com.isel.leic.ps.ionClassCode.services.AssignmentServicesError
import com.isel.leic.ps.ionClassCode.utils.Result
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
        return when (val assignment = assigmentService.getTeacherAssignmentInfoTeams(assignmentId = assignmentId)) {
            is Result.Problem -> problem(error = assignment.value)
            is Result.Success -> {
                assignment.value
                siren(
                    value = assignment.value,
                ) {
                    clazz(value = "assignment")
                    link(
                        rel = LinkRelation(value = "self"),
                        href = Uris.assigmentUri(courseId = courseId, classroomId = classroomId, assignmentId = assignmentId),
                        needAuthentication = true,
                    )
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
            AssignmentServicesError.InternalError -> Problem.internalError
        }
    }
}
