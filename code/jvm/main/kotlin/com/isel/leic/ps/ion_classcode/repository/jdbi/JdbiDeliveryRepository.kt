package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Delivery
import com.isel.leic.ps.ion_classcode.domain.input.DeliveryInput
import com.isel.leic.ps.ion_classcode.repository.DeliveryRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.sql.Timestamp

class JdbiDeliveryRepository(private val handle: Handle) : DeliveryRepository {
    override fun createDelivery(delivery: DeliveryInput): Int {
        return handle.createUpdate(
            """
                INSERT INTO DELIVERY (assignment_id, DUE_DATE, TAG_CONTROL) 
                VALUES (:assigmentId, :dueDate, :tagControl)
                RETURNING id
                """,
        )
            .bind("assigmentId", delivery.assigmentId)
            .bind("dueDate", delivery.dueDate)
            .bind("tagControl", delivery.tagControl)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
    }

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

    override fun getDeliveriesByAssigment(assignmentId: Int): List<Delivery> {
        return handle.createQuery(
            """
                SELECT * FROM DELIVERY
                WHERE assignment_id = :assigmentId
                ORDER BY due_date
                """,
        )
            .bind("assigmentId", assignmentId)
            .mapTo<Delivery>()
            .list()
    }

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

    override fun getTeamsByDelivery(deliveryId: Int): List<Int> {
        return handle.createQuery(
            """
                SELECT team.id FROM team
                JOIN repo on team.id = repo.team_id
                JOIN tags on repo.id = tags.repo_id
                JOIN delivery on tags.delivery_id = delivery.id
                WHERE delivery_id = :deliveryId and tags.is_delivered = true 
                """,
        )
            .bind("deliveryId", deliveryId)
            .mapTo<Int>()
            .list()
    }
}
