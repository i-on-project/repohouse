package com.isel.leic.ps.ionClassCode.repository.jdbi

import com.isel.leic.ps.ionClassCode.domain.Delivery
import com.isel.leic.ps.ionClassCode.domain.Team
import com.isel.leic.ps.ionClassCode.domain.input.DeliveryInput
import com.isel.leic.ps.ionClassCode.repository.DeliveryRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.sql.Timestamp

/**
 * Implementation of the Delivery methods
 */
class JdbiDeliveryRepository(private val handle: Handle) : DeliveryRepository {

    /**
     * Method to create a Delivery
     */
    override fun createDelivery(delivery: DeliveryInput): Delivery {
        val time = Timestamp(System.currentTimeMillis())
        val id = handle.createUpdate(
            """
                INSERT INTO DELIVERY (assignment_id, DUE_DATE, TAG_CONTROL, last_sync) 
                VALUES (:assigmentId, :dueDate, :tagControl,:time)
                RETURNING id
                """,
        )
            .bind("assigmentId", delivery.assignmentId)
            .bind("dueDate", delivery.dueDate)
            .bind("tagControl", delivery.tagControl)
            .bind("time", time)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
        return Delivery(id, delivery.dueDate, delivery.tagControl, delivery.assignmentId, time)
    }

    /**
     * Method to delete a Delivery
     */
    override fun deleteDelivery(deliveryId: Int) {
        handle.createUpdate(
            """
                DELETE FROM DELIVERY
                WHERE id = :deliveryId
                """,
        )
            .bind("deliveryId", deliveryId)
            .execute()
    }

    /**
     * Method to get a Delivery by is id
     */
    override fun getDeliveryById(deliveryId: Int): Delivery? {
        return handle.createQuery(
            """
                SELECT * FROM DELIVERY
                WHERE id = :deliveryId
                """,
        )
            .bind("deliveryId", deliveryId)
            .mapTo<Delivery>()
            .firstOrNull()
    }

    /**
     * Method to create all Deliveries by an assignment
     */
    override fun getDeliveriesByAssignment(assignmentId: Int): List<Delivery> {
        return handle.createQuery(
            """
                SELECT id, due_date, tag_control, assignment_id, last_sync
                FROM DELIVERY
                WHERE assignment_id = :assigmentId
                """,
        )
            .bind("assigmentId", assignmentId)
            .mapTo<Delivery>()
            .list()
    }

    /**
     * Method to update a Delivery due date
     */
    override fun updateDueDateFromDelivery(deliveryId: Int, dueDate: Timestamp) {
        handle.createUpdate(
            """
                UPDATE DELIVERY
                SET due_date = :dueDate::date
                WHERE id = :deliveryId
                """,
        )
            .bind("dueDate", dueDate)
            .bind("deliveryId", deliveryId)
            .execute()
    }

    /**
     * Method to update a Delivery tag control
     */
    override fun updateTagControlFromDelivery(deliveryId: Int, tagControl: String) {
        handle.createUpdate(
            """
                UPDATE DELIVERY
                SET tag_control = :tagControl
                WHERE id = :deliveryId
                """,
        )
            .bind("tagControl", tagControl)
            .bind("deliveryId", deliveryId)
            .execute()
    }

    /**
     * Method to get all Delivery teams
     */
    override fun getTeamsByDelivery(deliveryId: Int): List<Team> {
        return handle.createQuery(
            """
                SELECT team.id, team.name, team.is_created, team.assignment FROM team
                JOIN assignment  on team.assignment = assignment.id
                JOIN delivery  on assignment.id = delivery.assignment_id
                WHERE delivery.id = :deliveryId
                """,
        )
            .bind("deliveryId", deliveryId)
            .mapTo<Team>()
            .list()
    }

    override fun getTeamsDeliveredByDelivery(deliveryId: Int): List<Team> {
        return handle.createQuery(
            """
                SELECT distinct (team.id), team.name, team.is_created, team.assignment FROM team
                JOIN tags t on :deliveryId = t.delivery_id
                WHERE t.is_delivered = true
                """,
        )
            .bind("deliveryId", deliveryId)
            .mapTo<Team>()
            .list()
    }

    /**
     * Method to update a Delivery sync time
     */
    override fun updateSyncTimeFromDelivery(deliveryId: Int) {
        handle.createUpdate(
            """
                UPDATE DELIVERY
                SET last_sync = now()
                WHERE id = :deliveryId
                """,
        )
            .bind("deliveryId", deliveryId)
            .execute()
    }
}
