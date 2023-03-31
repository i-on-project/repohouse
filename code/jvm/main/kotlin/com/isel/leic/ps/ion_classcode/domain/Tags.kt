package com.isel.leic.ps.ion_classcode.domain

import java.sql.Timestamp

data class Tags(
    val id: Int,
    val name: String,
    val isDelivered: Boolean,
    val tagDate: Timestamp,
    val deliveryId: Int,
    val repoId: Int,
)
