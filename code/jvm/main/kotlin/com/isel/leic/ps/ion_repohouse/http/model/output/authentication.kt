package com.isel.leic.ps.ion_repohouse.http.model.output

import org.springframework.http.ResponseCookie

data class OAuthState(
    val value: String,
    val cookie: ResponseCookie
)

data class ClientToken(
    val access_token: String,
    val scope: String,
    val token_type: String
):OutputModel

data class GitHubUserInfo(
    val login: String,
    val url: String,
    val name: String
):OutputModel
