package com.isel.leic.ps.ion_classcode.domain

import java.sql.Timestamp

data class Outbox(
    val id: Int,
    val userId: Int,
    val otp: Int,
    val status: String,
    val expired_at: Timestamp,
    val sent_at: Timestamp? = null
){
    init {
        require(id > 0) { "Outbox id must be greater than 0" }
        require(userId > 0) { "User id must be greater than 0" }
        require(otp > 0) { "Outbox otp must be greater than 0" }
        require(status == "Pending" || status == "Sent") { "Outbox status cannot be blank" }
    }
}
