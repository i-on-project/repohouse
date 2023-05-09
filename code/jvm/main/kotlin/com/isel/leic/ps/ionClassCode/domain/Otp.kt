package com.isel.leic.ps.ionClassCode.domain

import java.sql.Timestamp

/**
 * Otp Domain Interface
 */
data class Otp(
    val userId: Int,
    val otp: Int,
    val expiredAt: Timestamp,
    val tries: Int = 0
)
