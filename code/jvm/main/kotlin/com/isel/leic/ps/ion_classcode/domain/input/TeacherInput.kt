package com.isel.leic.ps.ion_classcode.domain.input

/**
 * Teacher Input Interface
 */
data class TeacherInput(
    val email: String,
    val githubUsername: String,
    val githubId: Long,
    val token: String,
    val name: String,
    val githubToken: String,
) {
    init {
        require(name.isNotBlank() && name.length in (5..20)) { "Invalid name" }
        require(email.isNotBlank() && email.length in (5..100) && email.contains(char = '@')) { "Invalid email" }
        require(githubUsername.isNotBlank()) { "Invalid github username" }
        require(githubToken.isNotBlank()) { "Invalid github token" }
        require(githubId > 0) { "Invalid github id" }
    }
}
