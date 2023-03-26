package com.isel.leic.ps.ion_classcode.domain

sealed class User {
    abstract val id: Int?
    abstract val name: String
    abstract val email: String
    abstract val githubUsername: String
}

data class Student(
    override val name: String,
    override val email: String,
    override val id: Int,
    override val githubUsername: String,
    val schoolId: Int,
) : User() {
    init {
        require(name.isNotBlank() && name.length in (5..20)) { "Invalid name" }
        require(email.isNotBlank() && email.length in (5..30) && email.contains(char = '@')) { "Invalid email" }
        require(id > 0) { "Invalid id" }
        require(githubUsername.isNotBlank()) { "Invalid github username" }
    }
}

data class Teacher(
    override val name: String,
    override val email: String,
    override val id: Int,
    override val githubUsername: String,
    val githubToken: String,
    val isCreated: Boolean,
) : User() {
    init {
        require(name.isNotBlank() && name.length in (5..20)) { "Invalid name" }
        require(email.isNotBlank() && email.length in (5..30) && email.contains(char = '@')) { "Invalid email" }
        require(id > 0) { "Invalid id" }
        require(githubUsername.isNotBlank()) { "Invalid github username" }
    }
}
