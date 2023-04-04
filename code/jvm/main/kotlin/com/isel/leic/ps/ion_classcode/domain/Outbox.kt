package com.isel.leic.ps.ion_classcode.domain

import java.sql.Timestamp

data class Outbox(
    val userId: Int,
    val otp: Int,
    val status: String,
    val expiredAt: Timestamp,
    val sentAt: Timestamp? = null
)
