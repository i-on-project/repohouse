package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.LoginResponseDeserialization

data class CallBackResponse (
    val loginResponse: LoginResponseDeserialization? = null,
    val cookie: String? = null,
    val deepLink: String
)