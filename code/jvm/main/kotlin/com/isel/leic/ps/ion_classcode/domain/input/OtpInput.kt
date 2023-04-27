package com.isel.leic.ps.ion_classcode.domain.input

import java.sql.Timestamp

/**
 * Outbox Input Interface
 */
data class OtpInput(
    val userId: Int,
    val otp: Int
) {
    init {
        require(userId > 0) { "User id must be greater than 0" }
        require(otp in 100000..999999) { "Otp must be between 100000 and 999999" }
    }
}
