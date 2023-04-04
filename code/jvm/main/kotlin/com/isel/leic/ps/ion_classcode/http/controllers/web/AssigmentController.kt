package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.input.AssigmentInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.AssigmentCreatedOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.AssigmentOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.http.services.AssigmentServices
import com.isel.leic.ps.ion_classcode.http.services.AssigmentServicesError
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AssigmentController(
    private val assigmentService: AssigmentServices,
) {
    @GetMapping(Uris.ASSIGMENT_PATH)
    fun getAssigmentInfo(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assigmentId: Int,
    ): ResponseEntity<*> {
        return when (val assigment = assigmentService.getAssigmentInfo(assigmentId)) {
            is Either.Left -> problem(assigment.value)
            is Either.Right -> siren(value = AssigmentOutputModel(assigment.value.assigment, assigment.value.deliveries, assigment.value.teams)) {
                clazz("assigment")
                link(rel = LinkRelation("self"), href = Uris.assigmentUri(courseId, classroomId, assigmentId), needAuthentication = true)
                link(rel = LinkRelation("course"), href = Uris.courseUri(courseId), needAuthentication = true)
                link(rel = LinkRelation("classroom"), href = Uris.classroomUri(classroomId), needAuthentication = true)
                link(rel = LinkRelation("assigments"), href = Uris.assigmentsUri(courseId, classroomId), needAuthentication = true)
                assigment.value.deliveries.forEach {
                    link(rel = LinkRelation("delivery"), href = Uris.deliveryUri(courseId, classroomId, assigmentId, it.id), needAuthentication = true)
                }
                if (user is Teacher) {
                    assigment.value.teams.forEach {
                        link(rel = LinkRelation("team"), href = Uris.teamUri(courseId, classroomId, assigmentId, it.id), needAuthentication = true)
                    }
                    action("create-delivery", Uris.createDeliveryUri(courseId, classroomId, assigmentId), method = HttpMethod.POST, type = "application/json") {
                        hiddenField("assigmentId", assigmentId.toString())
                        textField("tag-control")
                        timestampField("due-date")
                    }
                }
                if (user is Student) {
                    when (val studentTeams = assigmentService.getAssigmentStudentTeams(assigmentId, user.id)) {
                        is Either.Left -> problem(studentTeams.value)
                        is Either.Right -> {
                            studentTeams.value.forEach {
                                link(rel = LinkRelation("team"), href = Uris.teamUri(courseId, classroomId, assigmentId, it.id), needAuthentication = true)
                            }
                        }
                    }
                }
            }
        }
    }

    @PostMapping(Uris.ASSIGMENTS_PATH)
    fun createAssignment(
        user: User,
        @PathVariable("courseId") courseId: Int,
        @PathVariable("classroomId") classroomId: Int,
        @RequestBody assigmentInfo: AssigmentInputModel,
    ): ResponseEntity<*> {
        return when (val assigment = assigmentService.createAssigment(assigmentInfo, user.id)) {
            is Either.Left -> problem(assigment.value)
            is Either.Right -> siren(value = AssigmentCreatedOutputModel(assigment.value)) {
                clazz("assigment")
                link(rel = LinkRelation("self"), href = Uris.assigmentUri(courseId, classroomId, assigment.value.id), needAuthentication = true)
                link(rel = LinkRelation("course"), href = Uris.courseUri(courseId), needAuthentication = true)
                link(rel = LinkRelation("classroom"), href = Uris.classroomUri(classroomId), needAuthentication = true)
                link(rel = LinkRelation("assigments"), href = Uris.assigmentsUri(courseId, classroomId), needAuthentication = true)
            }
        }
    }

    @DeleteMapping(Uris.DELETE_ASSIGMENT_PATH)
    fun deleteAssigment(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assigmentId: Int,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val delete = assigmentService.deleteAssigment(assigmentId)) {
            is Either.Left -> problem(delete.value)
            is Either.Right -> siren(value = delete.value) {
                clazz("assigment-deleted")
                link(rel = LinkRelation("course"), href = Uris.courseUri(courseId), needAuthentication = true)
                link(rel = LinkRelation("classroom"), href = Uris.classroomUri(classroomId), needAuthentication = true)
                link(rel = LinkRelation("assigments"), href = Uris.assigmentsUri(courseId, classroomId), needAuthentication = true)
            }
        }
    }

    private fun problem(error: AssigmentServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            AssigmentServicesError.NotTeacher -> Problem.notTeacher
            AssigmentServicesError.InvalidInput -> Problem.invalidInput
            AssigmentServicesError.AssigmentNotFound -> Problem.notFound
            AssigmentServicesError.AssigmentNotDeleted -> Problem.methodNotAllowed
            AssigmentServicesError.ClassroomArchived -> Problem.invalidOperation
            AssigmentServicesError.ClassroomNotFound -> Problem.notFound
        }
    }
}
