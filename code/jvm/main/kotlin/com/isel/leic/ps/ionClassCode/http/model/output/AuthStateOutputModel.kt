package com.isel.leic.ps.ionClassCode.http.model.output

/**
 * Represents an Auth State Output Model.
 */
data class AuthStateOutputModel(
    val user: String,
    val authenticated: Boolean,
    val githubId: Long,
    val userId: Int,
) : OutputModel
