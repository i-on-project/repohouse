package com.isel.leic.ps.ion_classcode.domain.input

/**
 * One Time Password Input Interface
 */
data class OtpInputModel(
    val otp: Int,
) {
    init {
        require(otp > 0) { "Otp must be greater than 0" }
    }
}
