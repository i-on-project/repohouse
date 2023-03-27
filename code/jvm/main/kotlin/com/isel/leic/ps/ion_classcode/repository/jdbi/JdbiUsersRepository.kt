package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.repository.UsersRepository
import org.jdbi.v3.core.Handle

class JdbiUsersRepository(
    private val handle: Handle,
) : UsersRepository {
    override fun createStudent(student: StudentInput): Int {
        val keys = handle.createUpdate(
            """
            INSERT INTO Users (email, github_username, github_id, token, name)
            VALUES (:email, :github_username, :github_id, :token, :name)
            RETURNING id
            """,
        )
            .bind("email", student.email)
            .bind("github_username", student.githubUsername)
            .bind("github_id", student.githubId)
            .bind("token", student.token)
            .bind("name", student.name)
            .executeAndReturnGeneratedKeys()
            .mapToMap()
            .findFirst()
            .orElseThrow { IllegalStateException("Failed to retrieve generated keys after inserting user") }

        val id = keys["id"] as Int
        handle.createUpdate(
            """
            INSERT INTO student (id, school_id)
            VALUES (:id, :schoolId)
            """,
        )
            .bind("id", id)
            .bind("schoolId", student.schoolId)
            .execute()
        return id
    }

    override fun createTeacher(teacher: TeacherInput): Int {
        val keys = handle.createUpdate(
            """
            INSERT INTO users (email, github_username, github_id, token, name)
            VALUES (:email, :github_username, :github_id, :token, :name)
            RETURNING id
            """,
        )
            .bind("email", teacher.email)
            .bind("github_username", teacher.githubUsername)
            .bind("github_id", teacher.githubId)
            .bind("token", teacher.token)
            .bind("name", teacher.name)
            .executeAndReturnGeneratedKeys()
            .mapToMap()
            .findFirst()
            .orElseThrow { IllegalStateException("Failed to retrieve generated keys after inserting user") }

        val id = keys["id"] as Int
        handle.createUpdate(
            """
            INSERT INTO Teacher (id, github_token)
            VALUES (:id, :github_token)
            """,
        )
            .bind("id", id)
            .bind("github_token", teacher.githubToken)
            .execute()
        return id
    }

    override fun getUserById(id: Int): User? {
        return helper(handle = handle, id = id)
    }

    override fun getUserByEmail(email: String): User? {
        val id = handle.createQuery(
            """
            SELECT id FROM users
            WHERE email = :email
            """,
        )
            .bind("email", email)
            .mapTo(Int::class.java)
            .firstOrNull() ?: return null
        return helper(handle = handle, id = id)
    }

    override fun getUserByToken(token: String): User? {
        val id = handle.createQuery(
            """
            SELECT id FROM users
            WHERE token = :token
            """,
        )
            .bind("token", token)
            .mapTo(Int::class.java)
            .firstOrNull() ?: return null

        return helper(handle = handle, id = id)
    }

    override fun deleteStudent(id: Int) {
        handle.createUpdate(
            """
            DELETE FROM student
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .execute()
    }

    override fun deleteTeacher(id: Int) {
        handle.createUpdate(
            """
            DELETE FROM teacher
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .execute()
    }
}
private fun helper(handle: Handle, id: Int): User? {
    return handle.createQuery(
        """
            SELECT name, email, users.id, github_username, github_id, is_created, school_id FROM Student
            JOIN users on users.id = student.id
            WHERE users.id = :id
            """,
    )
        .bind("id", id)
        .mapTo(Student::class.java)
        .firstOrNull() ?: handle.createQuery(
        """
            SELECT name, email, users.id, github_username, github_id, is_created FROM Teacher
            JOIN users on users.id = teacher.id
            WHERE users.id = :id
            """,
    )
        .bind("id", id)
        .mapTo(Teacher::class.java)
        .firstOrNull()
}
