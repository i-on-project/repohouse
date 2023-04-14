package com.isel.leic.ps.ion_classcode.domain.input

import java.sql.Timestamp

/**
 * Delivery Input Interface
 */
data class DeliveryInput(
    val dueDate: Timestamp,
    val assignmentId: Int,
    val tagControl: String,
) {
    fun isNotValid() = assignmentId <= 0 || tagControl.isEmpty()
}
