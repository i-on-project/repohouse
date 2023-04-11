package com.isel.leic.ps.ion_classcode.domain.input

import java.sql.Timestamp

/**
 * Delivery Input Interface
 */
data class DeliveryInput(
    val dueDate: Timestamp,
    val assigmentId: Int,
    val tagControl: String,
) {
    init {
        require(assigmentId > 0) { "Assigment id must be greater than 0" }
        require(tagControl.isNotEmpty()) { "Tag control must not be empty" }
    }
}
