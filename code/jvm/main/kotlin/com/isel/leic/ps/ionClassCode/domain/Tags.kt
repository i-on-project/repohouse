package com.isel.leic.ps.ionClassCode.domain

import java.sql.Timestamp

/**
 * Tags Domain Interface
 */
data class Tags(
    val id: Int,
    val name: String,
    val isDelivered: Boolean,
    val tagDate: Timestamp,
    val deliveryId: Int,
    val repoId: Int,
)
