package com.isel.leic.ps.ionClassCode.domain.input

/**
 * Otp Input Interface
 */
data class OutboxInput(
    val userId: Int,
) {
    init {
        require(userId > 0) { "User id must be greater than 0" }
    }
}
