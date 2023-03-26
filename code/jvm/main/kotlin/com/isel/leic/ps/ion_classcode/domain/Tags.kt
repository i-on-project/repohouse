package com.isel.leic.ps.ion_classcode.domain

data class Tags(
    val id: Int,
    val name: String,
    val isDelivered: Boolean,
    val tagDate: Int,
    val deliveryId: Int,
    val repoId: Int,
)
