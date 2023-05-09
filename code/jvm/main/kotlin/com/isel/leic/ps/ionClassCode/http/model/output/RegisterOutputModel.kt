package com.isel.leic.ps.ionClassCode.http.model.output

/**
 * Represents a Register Output Model.
 */
data class RegisterOutputModel(
    val name: String,
    val email: String,
    val GitHubUsername: String,
) : OutputModel
