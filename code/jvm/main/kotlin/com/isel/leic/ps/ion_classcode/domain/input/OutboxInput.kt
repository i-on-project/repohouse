package com.isel.leic.ps.ion_classcode.domain.input

import java.sql.Timestamp

data class OutboxInput(
    val userId:Int,
    val otp:Int,
) {
    init {
        require(userId > 0) { "User id must be greater than 0" }
        require(otp > 0) { "Otp must be greater than 0" }
    }
}
