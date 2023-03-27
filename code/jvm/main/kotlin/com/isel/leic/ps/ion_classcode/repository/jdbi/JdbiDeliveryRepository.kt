package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Delivery
import com.isel.leic.ps.ion_classcode.domain.input.DeliveryInput
import com.isel.leic.ps.ion_classcode.repository.DeliveryRepository
import org.jdbi.v3.core.Handle

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
            .execute()
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

    override fun getDeliveryById(deliveryId: Int): Delivery {
        return handle.createQuery(
            """
                SELECT * FROM DELIVERY
                WHERE id = :deliveryId
                """,
        )
            .bind("deliveryId", deliveryId)
            .mapTo(Delivery::class.java)
            .first()
    }

    override fun getDeliveriesByAssigment(assigmentId: Int): List<Delivery> {
        return handle.createQuery(
            """
                SELECT * FROM DELIVERY
                WHERE assignment_id = :assigmentId
                ORDER BY due_date
                """,
        )
            .bind("assigmentId", assigmentId)
            .mapTo(Delivery::class.java)
            .list()
    }

    override fun updateDueDateFromDelivery(deliveryId: Int, dueDate: String) {
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
}
