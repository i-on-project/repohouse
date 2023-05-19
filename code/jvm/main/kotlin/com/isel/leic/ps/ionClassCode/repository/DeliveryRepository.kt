package com.isel.leic.ps.ionClassCode.repository

import com.isel.leic.ps.ionClassCode.domain.Delivery
import com.isel.leic.ps.ionClassCode.domain.Team
import com.isel.leic.ps.ionClassCode.domain.input.DeliveryInput
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
    fun getTeamsDeliveredByDelivery(deliveryId: Int): List<Team>
    fun updateSyncTimeFromDelivery(deliveryId: Int)
}
