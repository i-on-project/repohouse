package com.isel.leic.ps.ion_classcode.http.model.output

/**
 * Represents a Register Output Model.
 */
data class RegisterOutputModel(
    val name: String,
    val email: String,
    val GitHubUsername: String,
) : OutputModel
