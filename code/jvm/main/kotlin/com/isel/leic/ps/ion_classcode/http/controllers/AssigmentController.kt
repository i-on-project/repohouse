package com.isel.leic.ps.ion_classcode.http.controllers

import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.input.AssigmentInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.AssigmentCreatedOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.http.services.AssigmentServices
import com.isel.leic.ps.ion_classcode.http.services.AssigmentServicesError
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class AssigmentController(
    private val assigmentService: AssigmentServices,
) {

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
                link(rel = LinkRelation("self"), href = Uris.assigmentsUri(), needAuthentication = true)
            }
        }
    }

    private fun problem(error: AssigmentServicesError): ResponseEntity<ErrorMessageModel> {
        return when(error) {
            AssigmentServicesError.NotTeacher -> Problem.notTeacher
            AssigmentServicesError.InvalidInput -> Problem.invalidInput
        }
    }
}
