package com.isel.leic.ps.ion_classcode.domain

/**
 * A response with the two tokens needed to be sent to the mobile client
 */
data class Tokens(
    val accessToken: String,
    val classCodeToken: String
)
