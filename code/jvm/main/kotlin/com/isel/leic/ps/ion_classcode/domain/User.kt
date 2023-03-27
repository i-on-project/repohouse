package com.isel.leic.ps.ion_classcode.domain

sealed class User {
    abstract val id: Int?
    abstract val name: String
    abstract val email: String
    abstract val githubUsername: String
    abstract val isCreated: Boolean
    abstract val githubId: Int
}

data class Student(
    override val name: String,
    override val email: String,
    override val id: Int,
    override val githubUsername: String,
    override val githubId: Int,
    override val isCreated: Boolean,
    val schoolId: Int,
) : User()

data class Teacher(
    override val name: String,
    override val email: String,
    override val id: Int,
    override val githubUsername: String,
    override val githubId: Int,
    override val isCreated: Boolean,
) : User()
