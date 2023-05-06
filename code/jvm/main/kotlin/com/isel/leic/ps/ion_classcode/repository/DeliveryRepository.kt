package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Delivery
import com.isel.leic.ps.ion_classcode.domain.Team
import com.isel.leic.ps.ion_classcode.domain.input.DeliveryInput
import java.sql.Timestamp

/**
 * Repository functions for Delivery Repository
 */
interface DeliveryRepository {
    fun createDelivery(delivery: DeliveryInput): Delivery //
    fun deleteDelivery(deliveryId: Int)
    fun getDeliveryById(deliveryId: Int): Delivery? //
    fun getDeliveriesByAssignment(assignmentId: Int): List<Delivery> //
    fun updateDueDateFromDelivery(deliveryId: Int, dueDate: Timestamp) //
    fun updateTagControlFromDelivery(deliveryId: Int, tagControl: String)
    fun getTeamsByDelivery(deliveryId: Int): List<Team>
}
