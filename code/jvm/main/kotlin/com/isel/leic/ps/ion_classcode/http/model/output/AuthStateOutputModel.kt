package com.isel.leic.ps.ion_classcode.http.model.output

data class AuthStateOutputModel(
    val user: String,
    val authenticated: Boolean,
    val githubId: Long,
    val userId: Int,
) : OutputModel
