package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.DeliveryInput
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.output.DeliveryDeleteOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.DeliveryOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.http.services.DeliveryServices
import com.isel.leic.ps.ion_classcode.http.services.DeliveryServicesError
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
class DeliveryController(
    private val deliveryServices: DeliveryServices,
) {
    // TODO: syncDelivery

    @GetMapping(Uris.DELIVERY_PATH)
    fun getDeliveryInfo(
        user: User,
        @PathVariable deliveryId: Int,
        @PathVariable assigmentId: Int,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        return when (val delivery = deliveryServices.getDeliveryInfo(deliveryId)) {
            is Either.Left -> problem(delivery.value)
            is Either.Right -> siren(DeliveryOutputModel(delivery = delivery.value.delivery, teamsDelivered = delivery.value.teamsDelivered, teamsNotDelivered = delivery.value.teamsNotDelivered)) {
                link(href = Uris.deliveryUri(courseId, classroomId, assigmentId, deliveryId), rel = LinkRelation("self"), needAuthentication = true)
                link(href = Uris.deliverysUri(courseId, classroomId, assigmentId), rel = LinkRelation("deliveries"), needAuthentication = true)
                delivery.value.teamsNotDelivered.forEach {
                    link(href = Uris.teamUri(courseId, classroomId, assigmentId, it.id), rel = LinkRelation("team"), needAuthentication = true)
                }
                delivery.value.teamsDelivered.forEach {
                    link(href = Uris.teamUri(courseId, classroomId, assigmentId, it.id), rel = LinkRelation("team"), needAuthentication = true)
                }
                if (user is Teacher) {
                    action(
                        name = "edit-delivery",
                        href = Uris.editDeliveryUri(courseId, classroomId, assigmentId, deliveryId),
                        method = HttpMethod.POST,
                        type = "application/json",
                    ) {
                        timestampField(name = "dueDate", delivery.value.delivery.dueDate)
                        textField(name = "tagControl", delivery.value.delivery.tagControl)
                    }
                    action(
                        name = "delete-delivery",
                        href = Uris.deliveryUri(courseId, classroomId, assigmentId, deliveryId),
                        method = HttpMethod.DELETE,
                        type = "application/json",
                    ) {}
                    action(
                        name = "sync-delivery",
                        href = Uris.syncDeliveryUri(courseId, classroomId, assigmentId, deliveryId),
                        method = HttpMethod.POST,
                        type = "application/json",
                    ) {}
                }
            }
        }
    }

    @PostMapping(Uris.CREATE_DELIVERY_PATH)
    fun createDelivery(
        user: User,
        @PathVariable assigmentId: Int,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
        @RequestBody deliveryInfo: DeliveryInput,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val deliveryId = deliveryServices.createDelivery(deliveryInfo,user.id)){
            is Either.Left -> problem(deliveryId.value)
            is Either.Right -> when(val delivery = deliveryServices.getDeliveryInfo(deliveryId.value)){
                is Either.Left -> problem(delivery.value)
                is Either.Right -> siren(DeliveryOutputModel(delivery.value.delivery,delivery.value.teamsDelivered,delivery.value.teamsNotDelivered)){
                    link(href = Uris.deliveryUri(courseId, classroomId, assigmentId, deliveryId.value), rel = LinkRelation("self"), needAuthentication = true)
                    link(href = Uris.deliverysUri(courseId, classroomId, assigmentId), rel = LinkRelation("deliveries"), needAuthentication = true)
                    delivery.value.teamsNotDelivered.forEach {
                        link(href = Uris.teamUri(courseId, classroomId, assigmentId, it.id), rel = LinkRelation("team"), needAuthentication = true)
                    }
                    delivery.value.teamsDelivered.forEach {
                        link(href = Uris.teamUri(courseId, classroomId, assigmentId, it.id), rel = LinkRelation("team"), needAuthentication = true)
                    }
                    action(
                        name = "edit-delivery",
                        href = Uris.editDeliveryUri(courseId, classroomId, assigmentId, deliveryId.value),
                        method = HttpMethod.POST,
                        type = "application/json",
                    ) {
                        timestampField(name = "dueDate", delivery.value.delivery.dueDate)
                        textField(name = "tagControl", delivery.value.delivery.tagControl)
                    }
                    action(
                        name = "delete-delivery",
                        href = Uris.deliveryUri(courseId, classroomId, assigmentId, deliveryId.value),
                        method = HttpMethod.DELETE,
                        type = "application/json",
                    ) {}
                    action(
                        name = "sync-delivery",
                        href = Uris.syncDeliveryUri(courseId, classroomId, assigmentId, deliveryId.value),
                        method = HttpMethod.POST,
                        type = "application/json",
                    ) {}
                }
            }
        }
    }

    @DeleteMapping(Uris.DELIVERY_PATH)
    fun deleteDelivery(
        user: User,
        @PathVariable deliveryId: Int,
        @PathVariable assigmentId: Int,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val delivery = deliveryServices.deleteDelivery(deliveryId,user.id)) {
            is Either.Left -> problem(delivery.value)
            is Either.Right -> siren(DeliveryDeleteOutputModel(deliveryId,delivery.value)){
                clazz("delivery-deleted")
                link(rel = LinkRelation("assigment"), href = Uris.assigmentUri(courseId,classroomId, assigmentId), needAuthentication = true)
            }
        }
    }

    @PostMapping(Uris.EDIT_DELIVERY_PATH)
    fun editDelivery(
        user: User,
        @PathVariable deliveryId: Int,
        @PathVariable assigmentId: Int,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
        @RequestBody deliveryInfo: DeliveryInput,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val updateDelivery = deliveryServices.updateDelivery(deliveryId,deliveryInfo,user.id)){
            is Either.Left -> problem(updateDelivery.value)
            is Either.Right -> when(val delivery = deliveryServices.getDeliveryInfo(deliveryId)){
                is Either.Left -> problem(delivery.value)
                is Either.Right -> siren(DeliveryOutputModel(delivery.value.delivery,delivery.value.teamsDelivered,delivery.value.teamsNotDelivered)){
                    link(href = Uris.deliveryUri(courseId, classroomId, assigmentId, deliveryId), rel = LinkRelation("delivery"), needAuthentication = true)
                }
            }
        }
    }

    @PostMapping(Uris.SYNC_DELIVERY_PATH)
    fun syncDelivery(
        user: User,
        @PathVariable deliveryId: Int,
        @PathVariable assigmentId: Int,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return TODO()
    }

    private fun problem(error: DeliveryServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            DeliveryServicesError.DeliveryNotFound -> Problem.notFound
            DeliveryServicesError.InvalidInput -> Problem.invalidInput
            DeliveryServicesError.NotTeacher -> Problem.notTeacher
            DeliveryServicesError.DeliveryWithTeams -> Problem.invalidOperation
        }
    }
}
