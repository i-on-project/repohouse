package com.isel.leic.ps.ionClassCode.http.model.output

import org.springframework.http.ResponseCookie

/**
 * Represents a OAuth State.
 */
data class OAuthState(
    val value: String,
    val cookie: ResponseCookie,
)

/**
 * Represents a Client Token.
 */
data class ClientToken(
    val access_token: String,
    val scope: String,
    val token_type: String,
)

/**
 * Represents a GitHub User Info.
 */
data class GitHubUserInfo(
    val login: String,
    val id: Long,
    val url: String,
    val name: String
)

/**
 * Represents a GitHub User Email.
 */
data class GitHubUserEmail(
    val email: String,
    val verified: Boolean,
    val primary: Boolean,
    val visibility: String?
)
