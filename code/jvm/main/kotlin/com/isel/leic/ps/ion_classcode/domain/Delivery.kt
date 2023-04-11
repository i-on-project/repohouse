package com.isel.leic.ps.ion_classcode.domain

import java.sql.Timestamp

/**
 * Delivery Domain Interface
 */
data class Delivery(
    val id: Int,
    val dueDate: Timestamp,
    val tagControl: String,
    val assignmentId: Int,
)
