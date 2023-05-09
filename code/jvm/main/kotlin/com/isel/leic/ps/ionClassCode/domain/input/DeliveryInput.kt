package com.isel.leic.ps.ionClassCode.domain.input

import java.sql.Timestamp

/**
 * Delivery Input Interface
 */
data class DeliveryInput(
    val dueDate: Timestamp,
    val assignmentId: Int,
    val tagControl: String,
) {
    fun isNotValid() = tagControl.isEmpty()
}
