package com.isel.leic.ps.ionClassCode.repository

import com.isel.leic.ps.ionClassCode.domain.input.DeliveryInput
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiDeliveryRepository
import com.isel.leic.ps.ionClassCode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class DeliveryRepositoryTests {

    @Test
    fun `can create delivery`() = testWithHandleAndRollback { handle ->
        val deliveryRepository = JdbiDeliveryRepository(handle = handle)
        val assignmentId = 1
        val created = deliveryRepository.createDelivery(delivery = DeliveryInput(assignmentId = assignmentId, dueDate = Timestamp.from(Instant.now()), tagControl = "tag"))
        val delivery = deliveryRepository.getDeliveryById(deliveryId = created.id)
        assert(delivery != null)
    }

    @Test
    fun `can delete a delivery`() = testWithHandleAndRollback { handle ->
        val deliveryRepository = JdbiDeliveryRepository(handle = handle)
        val deliveryId = 3
        deliveryRepository.deleteDelivery(deliveryId = deliveryId)
        val delivery = deliveryRepository.getDeliveryById(deliveryId = deliveryId)
        assert(delivery == null)
    }

    @Test
    fun `can get delivery`() = testWithHandleAndRollback { handle ->
        val deliveryRepository = JdbiDeliveryRepository(handle = handle)
        val deliveryId = 1
        val tagControl = "tag"
        val delivery = deliveryRepository.getDeliveryById(deliveryId = deliveryId) ?: fail("Delivery not found")
        assert(delivery.tagControl == tagControl)
    }

    @Test
    fun `can get list of deliveries using assignmentId`() = testWithHandleAndRollback { handle ->
        val deliveryRepository = JdbiDeliveryRepository(handle = handle)
        val assignmentId = 1
        val list = deliveryRepository.getDeliveriesByAssignment(assignmentId = assignmentId)
        assert(list.size == 2)
    }

    @Test
    fun `can update a due date of a delivery`() = testWithHandleAndRollback { handle ->
        val deliveryRepository = JdbiDeliveryRepository(handle = handle)
        val deliveryId = 1
        val newTimestamp = Timestamp.from(Instant.from(ZonedDateTime.of(2023, 1, 19, 1, 1, 1, 1, ZoneId.of("UTC"))))
        deliveryRepository.updateDueDateFromDelivery(deliveryId = deliveryId, dueDate = newTimestamp)
        val delivery = deliveryRepository.getDeliveryById(deliveryId = deliveryId) ?: fail("Delivery not found")

        assert(delivery.dueDate.toLocalDateTime()?.dayOfWeek == newTimestamp.toLocalDateTime().dayOfWeek)
        assert(delivery.dueDate.toLocalDateTime()?.dayOfMonth == newTimestamp.toLocalDateTime().dayOfMonth)
        assert(delivery.dueDate.toLocalDateTime()?.dayOfYear == newTimestamp.toLocalDateTime().dayOfYear)
    }

    @Test
    fun `can update a tag control of a delivery`() = testWithHandleAndRollback { handle ->
        val deliveryRepository = JdbiDeliveryRepository(handle = handle)
        val newTag = "newTag"
        val deliveryId = 1
        deliveryRepository.updateTagControlFromDelivery(deliveryId = deliveryId, tagControl = newTag)
        val delivery = deliveryRepository.getDeliveryById(deliveryId = deliveryId) ?: fail("Delivery not found")
        assert(delivery.tagControl == newTag)
    }

    @Test
    fun `can get teams of a delivery`() = testWithHandleAndRollback { handle ->
        val deliveryRepository = JdbiDeliveryRepository(handle = handle)
        val deliveryId = 1
        val teams = deliveryRepository.getTeamsByDelivery(deliveryId = deliveryId)
        assert(teams.size == 2)
    }
}
