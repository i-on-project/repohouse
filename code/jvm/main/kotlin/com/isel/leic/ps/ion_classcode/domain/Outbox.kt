package com.isel.leic.ps.ion_classcode.domain

import java.sql.Timestamp

/**
 * Outbox Domain Interface
 */
data class Outbox(
    val userId: Int,
    val status: String,
    val sentAt: Timestamp? = null
)
