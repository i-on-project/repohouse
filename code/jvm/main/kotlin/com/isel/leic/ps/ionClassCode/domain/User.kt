package com.isel.leic.ps.ionClassCode.domain

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

data class StudentWithoutToken(
    val name: String,
    val email: String,
    val id: Int,
    val githubUsername: String,
    val githubId: Long,
    val isCreated: Boolean,
    val schoolId: Int?,
)

/**
 * Pending Student Domain Interface
 */
data class PendingStudent(
    override val name: String,
    override val email: String,
    override val id: Int,
    override val githubUsername: String,
    override val githubId: Long,
    override val isCreated: Boolean = false,
    override val token: String,
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

data class TeacherWithoutToken(
    val name: String,
    val email: String,
    val id: Int,
    val githubUsername: String,
    val githubId: Long,
    val isCreated: Boolean,
)

/**
 * Pending Teacher Domain Interface
 */
data class PendingTeacher(
    override val name: String,
    override val email: String,
    override val id: Int,
    override val githubUsername: String,
    override val githubId: Long,
    override val token: String,
    override val isCreated: Boolean = false,
    val githubToken: String,
) : User()
