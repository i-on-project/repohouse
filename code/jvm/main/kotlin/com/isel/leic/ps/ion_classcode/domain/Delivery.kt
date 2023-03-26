package com.isel.leic.ps.ion_classcode.domain

data class Delivery(
    val id: Int,
    val dueDate: Long,
    val assigmentId: Int,
    val tagControl: String,
)
