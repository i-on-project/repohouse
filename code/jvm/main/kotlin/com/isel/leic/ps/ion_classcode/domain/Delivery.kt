package com.isel.leic.ps.ion_classcode.domain

import java.sql.Timestamp

data class Delivery(
    val id: Int,
    val dueDate: Timestamp,
    val tagControl: String,
    val assignmentId: Int,
)
