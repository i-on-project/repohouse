package com.isel.leic.ps.ionClassCode.domain.input

/**
 * One Time Password Input Interface
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
