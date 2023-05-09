package com.isel.leic.ps.ionClassCode.http.model.output

/**
 * Represents an AuthRedirect State.
 */
data class AuthRedirect(
    val message: String = "Please initiate the authentication process.",
    val url: String,
) : OutputModel
