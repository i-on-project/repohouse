package com.isel.leic.ps.ionClassCode.http.controllers.web

import com.isel.leic.ps.ionClassCode.domain.Teacher
import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.domain.input.DeliveryInput
import com.isel.leic.ps.ionClassCode.http.Uris
import com.isel.leic.ps.ionClassCode.http.model.output.DeliveryDeleteOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.DeliveryOutputModel
import com.isel.leic.ps.ionClassCode.http.model.problem.Problem
import com.isel.leic.ps.ionClassCode.infra.LinkRelation
import com.isel.leic.ps.ionClassCode.infra.siren
import com.isel.leic.ps.ionClassCode.services.DeliveryServices
import com.isel.leic.ps.ionClassCode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Delivery Controller
 * All the write operations are done by the teacher and need to ensure the classroom is not archived
 */
@RestController
class DeliveryController(
    private val deliveryServices: DeliveryServices,
) {

    /**
     * Get all information about a delivery
     */
    @GetMapping(Uris.DELIVERY_PATH)
    fun getDeliveryInfo(
        user: User,
        @PathVariable deliveryId: Int,
        @PathVariable assignmentId: Int,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        return when (val delivery = deliveryServices.getDeliveryInfo(deliveryId)) {
            is Result.Problem -> deliveryServices.problem(delivery.value)
            is Result.Success -> siren(DeliveryOutputModel(delivery = delivery.value.delivery, teamsDelivered = delivery.value.teamsDelivered, teamsNotDelivered = delivery.value.teamsNotDelivered)) {
                link(href = Uris.deliveryUri(courseId, classroomId, assignmentId, deliveryId), rel = LinkRelation("self"), needAuthentication = true)
            }
        }
    }

    /**
     * Create a new delivery
     */
    @PostMapping(Uris.CREATE_DELIVERY_PATH)
    fun createDelivery(
        user: User,
        @PathVariable assignmentId: Int,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
        @RequestBody deliveryInfo: DeliveryInput,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val deliveryId = deliveryServices.createDelivery(deliveryInfo, user.id)) {
            is Result.Problem -> deliveryServices.problem(deliveryId.value)
            is Result.Success -> when (val delivery = deliveryServices.getDeliveryInfo(deliveryId.value.id)) {
                is Result.Problem -> deliveryServices.problem(delivery.value)
                is Result.Success -> siren(DeliveryOutputModel(delivery.value.delivery, delivery.value.teamsDelivered, delivery.value.teamsNotDelivered)) {
                    link(href = Uris.deliveryUri(courseId, classroomId, assignmentId, deliveryId.value.id), rel = LinkRelation("self"), needAuthentication = true)
                }
            }
        }
    }

    /**
     * Delete a delivery
     */
    @DeleteMapping(Uris.DELIVERY_PATH)
    fun deleteDelivery(
        user: User,
        @PathVariable deliveryId: Int,
        @PathVariable assignmentId: Int,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val delivery = deliveryServices.deleteDelivery(deliveryId, user.id)) {
            is Result.Problem -> deliveryServices.problem(delivery.value)
            is Result.Success -> siren(DeliveryDeleteOutputModel(deliveryId, delivery.value)) {
                clazz("delivery-deleted")
                link(href = Uris.deliveriesUri(courseId, classroomId, assignmentId), rel = LinkRelation("self"), needAuthentication = true)
            }
        }
    }

    /**
     * Edit a delivery
     */
    @PostMapping(Uris.EDIT_DELIVERY_PATH)
    fun editDelivery(
        user: User,
        @PathVariable deliveryId: Int,
        @PathVariable assignmentId: Int,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
        @RequestBody deliveryInfo: DeliveryInput,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val updateDelivery = deliveryServices.updateDelivery(deliveryId, deliveryInfo, user.id)) {
            is Result.Problem -> deliveryServices.problem(updateDelivery.value)
            is Result.Success -> when (val delivery = deliveryServices.getDeliveryInfo(deliveryId)) {
                is Result.Problem -> deliveryServices.problem(delivery.value)
                is Result.Success -> siren(DeliveryOutputModel(delivery.value.delivery, delivery.value.teamsDelivered, delivery.value.teamsNotDelivered)) {
                    link(href = Uris.deliveryUri(courseId, classroomId, assignmentId, deliveryId), rel = LinkRelation("self"), needAuthentication = true)
                }
            }
        }
    }

    /**
     * Sync a delivery with the GitHub truth
     */
    @PostMapping(Uris.SYNC_DELIVERY_PATH)
    suspend fun syncDelivery(
        user: User,
        @PathVariable deliveryId: Int,
        @PathVariable assignmentId: Int,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val syncDelivery = deliveryServices.syncDelivery(deliveryId, user.id, courseId)) {
            is Result.Problem -> deliveryServices.problem(syncDelivery.value)
            is Result.Success -> when (val delivery = deliveryServices.getDeliveryInfo(deliveryId)) {
                is Result.Problem -> deliveryServices.problem(delivery.value)
                is Result.Success -> siren(DeliveryOutputModel(delivery.value.delivery, delivery.value.teamsDelivered, delivery.value.teamsNotDelivered)) {
                    link(href = Uris.deliveryUri(courseId, classroomId, assignmentId, deliveryId), rel = LinkRelation("self"), needAuthentication = true)
                }
            }
        }
    }
}
