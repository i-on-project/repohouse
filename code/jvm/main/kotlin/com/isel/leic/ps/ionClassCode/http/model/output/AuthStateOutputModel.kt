package com.isel.leic.ps.ionClassCode.http.model.output

data class AuthStateOutputModel(
    val user: String,
    val authenticated: Boolean,
    val githubId: Long,
    val userId: Int,
) : OutputModel
