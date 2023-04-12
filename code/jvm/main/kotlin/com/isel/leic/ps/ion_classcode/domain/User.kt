package com.isel.leic.ps.ion_classcode.domain

/**
 * User Interface
 */
sealed class User {
    abstract val id: Int
    abstract val name: String
    abstract val email: String
    abstract val githubUsername: String
    abstract val isCreated: Boolean
    abstract val githubId: Long
    abstract val token: String
}

/**
 * Student Domain Interface
 */
data class Student(
    override val name: String,
    override val email: String,
    override val id: Int,
    override val githubUsername: String,
    override val githubId: Long,
    override val isCreated: Boolean,
    override val token: String,
    val schoolId: Int?,
) : User()

/**
 * Teacher Domain Interface
 */
data class Teacher(
    override val name: String,
    override val email: String,
    override val id: Int,
    override val githubUsername: String,
    override val githubId: Long,
    override val token: String,
    override val isCreated: Boolean,
) : User()
