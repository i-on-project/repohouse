package com.isel.leic.ps.ion_classcode.domain.input

data class TeacherInput(
    val email: String,
    val githubUsername: String,
    val githubId: Int,
    val token: String,
    val name: String,
    val githubToken: String,
) {
    init {
        require(name.isNotBlank() && name.length in (5..20)) { "Invalid name" }
        require(email.isNotBlank() && email.length in (5..30) && email.contains(char = '@')) { "Invalid email" }
        require(githubUsername.isNotBlank()) { "Invalid github username" }
        require(githubToken.isNotBlank()) { "Invalid github token" }
        require(githubId > 0) { "Invalid github id" }
    }
}