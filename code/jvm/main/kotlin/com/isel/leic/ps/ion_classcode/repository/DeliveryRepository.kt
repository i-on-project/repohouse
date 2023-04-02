package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Delivery
import com.isel.leic.ps.ion_classcode.domain.input.DeliveryInput
import java.sql.Timestamp

interface DeliveryRepository {
    fun createDelivery(delivery: DeliveryInput): Int
    fun deleteDelivery(deliveryId: Int)
    fun getDeliveryById(deliveryId: Int): Delivery?
    fun getDeliveriesByAssigment(assigmentId: Int): List<Delivery>
    fun updateDueDateFromDelivery(deliveryId: Int, dueDate: Timestamp)
    fun updateTagControlFromDelivery(deliveryId: Int, tagControl: String)
}
