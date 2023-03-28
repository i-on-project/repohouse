package com.isel.leic.ps.ion_classcode.http.model.output

import org.springframework.http.ResponseCookie

data class OAuthState(
    val value: String,
    val cookie: ResponseCookie,
)

data class ClientToken(
    val access_token: String,
    val scope: String,
    val token_type: String,
)

data class GitHubUserInfo(
    val login: String,
    val id: Long,
    val url: String,
    val name: String
)

data class GitHubUserEmail(
    val email: String,
    val verified: Boolean,
    val primary: Boolean,
    val visibility: String?
)

