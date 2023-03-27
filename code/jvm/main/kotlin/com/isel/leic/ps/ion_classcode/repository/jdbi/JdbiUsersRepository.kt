package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.repository.UsersRepository
import org.jdbi.v3.core.Handle
import org.springframework.stereotype.Component

class JdbiUsersRepository(
    private val handle: Handle,
) : UsersRepository {
    override fun createStudent(student: Student): Int {
        val id = handle.createUpdate(
            """
            INSERT INTO users (name, email, github_username)
            VALUES (:name, :email, :githubUsername)
            RETURNING id
            """,
        )
            .bind("name", student.name)
            .bind("email", student.email)
            .bind("githubUsername", student.githubUsername)
            .execute()

        return handle.createUpdate(
            """
            INSERT INTO student (id, school_id)
            VALUES (:id, :schoolId)
            RETURNING id
            """,
        )
            .bind("id", id)
            .bind("schoolId", student.schoolId)
            .execute()
    }

    override fun getStudentById(id: Int): Student? {
        return handle.createQuery(
            """
            SELECT name, email,users.id,github_username,school_id FROM Student
            JOIN users on users.id = student.id
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo(Student::class.java)
            .firstOrNull()
    }

    override fun getStudentByEmail(email: String): Student? {
        return handle.createQuery(
            """
            SELECT name, email,users.id,github_username,school_id FROM Student
            JOIN users on users.id = student.id
            WHERE email = :email
            """,
        )
            .bind("email", email)
            .mapTo(Student::class.java)
            .firstOrNull()
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

        return handle.createQuery(
            """
            SELECT name, email,users.id,github_username,school_id FROM Student
            JOIN users on users.id = student.id
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo(Student::class.java)
            .firstOrNull() ?: handle.createQuery(
            """
            SELECT name, email,users.id,github_username,github_token,is_created FROM Teacher
            JOIN users on users.id = teacher.id
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo(Teacher::class.java)
            .firstOrNull()
    }

    override fun createTeacher(teacher: Teacher): Int {
        val id = handle.createUpdate(
            """
            INSERT INTO users (name, email, github_username)
            VALUES (:name, :email, :githubUsername)
            RETURNING id
            """,
        )
            .bind("name", teacher.name)
            .bind("email", teacher.email)
            .bind("githubUsername", teacher.githubUsername)
            .execute()
        return handle.createUpdate(
            """
            INSERT INTO Teacher (id, github_token, is_created)
            VALUES (:id, :githubToken, :isCreated)
            RETURNING id
            """,
        )
            .bind("id", id)
            .bind("githubToken", teacher.githubToken)
            .bind("isCreated", teacher.isCreated)
            .execute()
    }

    override fun getTeacherById(id: Int): Teacher? {
        return handle.createQuery(
            """
            SELECT name, email,users.id,github_username,github_token,is_created FROM Teacher
            JOIN users on users.id = teacher.id
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo(Teacher::class.java)
            .firstOrNull()
    }

    override fun getTeacherByEmail(email: String): Teacher? {
        return handle.createQuery(
            """
            SELECT name, email,users.id,github_username,github_token,is_created FROM Teacher
            JOIN users on users.id = teacher.id
            WHERE email = :email
            """,
        )
            .bind("email", email)
            .mapTo(Teacher::class.java)
            .firstOrNull()
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
