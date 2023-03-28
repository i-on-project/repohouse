package com.isel.leic.ps.ion_classcode.domain.input

data class StudentInput(
    val name: String,
    val email: String,
    val githubUsername: String,
    val schoolId: Int,
    val token: String,
    val githubId: Long,
) {
    init {
        require(name.isNotBlank() && name.length in (5..20)) { "Invalid name" }
        require(email.isNotBlank() && email.length in (5..30) && email.contains(char = '@')) { "Invalid email" }
        require(githubUsername.isNotBlank()) { "Invalid github username" }
        require(schoolId > 0) { "Invalid school id" }
        require(token.isNotBlank()) { "Invalid token" }
        require(githubId > 0) { "Invalid github id" }
    }
}
