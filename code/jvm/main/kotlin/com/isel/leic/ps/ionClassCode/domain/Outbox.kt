package com.isel.leic.ps.ionClassCode.domain

import java.sql.Timestamp

/**
 * Outbox Domain Interface
 */
data class Outbox(
    val userId: Int,
    val status: String,
    val sentAt: Timestamp? = null
)
