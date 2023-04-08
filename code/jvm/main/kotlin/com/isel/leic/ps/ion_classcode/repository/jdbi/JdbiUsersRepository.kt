package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.repository.UsersRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiUsersRepository(
    private val handle: Handle,
) : UsersRepository {
    override fun createStudent(student: StudentInput): Int {
        val id = handle.createUpdate(
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
            .mapTo<Int>()
            .first()

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

    override fun getAllStudents(): List<Student> {
        return handle.createQuery(
            """
            SELECT name, email, Users.id, github_username, github_id, is_created, school_id,token FROM Student
            JOIN Users on Users.id = Student.id
            """,
        )
            .mapTo<Student>()
            .list()
    }

    override fun getAllTeachers(): List<Teacher> {
        return handle.createQuery(
            """
            SELECT name, email, Users.id, github_username, github_id, is_created, github_token,token FROM Teacher
            JOIN Users on Users.id = Teacher.id
            """,
        )
            .mapTo<Teacher>()
            .list()
    }

    override fun getUserById(id: Int): User? {
        return helper(handle = handle, id = id)
    }

    override fun getUserByEmail(email: String): User? {
        val id = handle.createQuery(
            """
            SELECT id FROM Users
            WHERE email = :email
            """,
        )
            .bind("email", email)
            .mapTo<Int>()
            .firstOrNull() ?: return null
        return helper(handle = handle, id = id)
    }

    override fun getUserByGithubId(githubId: Long): User? {
        val id = handle.createQuery(
            """
            SELECT id FROM Users
            WHERE github_id = :github_id
            """,
        )
            .bind("github_id", githubId)
            .mapTo<Int>()
            .firstOrNull() ?: return null
        return helper(handle = handle, id = id)
    }

    override fun getUserByToken(token: String): User? {
        val id = handle.createQuery(
            """
            SELECT id FROM Users
            WHERE token = :token
            """,
        )
            .bind("token", token)
            .mapTo<Int>()
            .firstOrNull() ?: return null

        return helper(handle = handle, id = id)
    }

    override fun getStudentSchoolId(id: Int): Int? {
        return handle.createQuery(
            """
            SELECT school_id FROM Student
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo<Int>()
            .firstOrNull()
    }

    override fun updateStudentSchoolId(userId: Int, schoolId: Int) {
        handle.createUpdate(
            """
            UPDATE student SET school_id = :school_id
            WHERE id = :id
            """,
        )
            .bind("id", userId)
            .bind("school_id", schoolId)
            .execute()
    }

    override fun createTeacher(teacher: TeacherInput): Int {
        val id = handle.createUpdate(
            """
            INSERT INTO Users (email, github_username, github_id, token, name)
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
            .mapTo<Int>()
            .first()

        handle.createUpdate(
            """
            INSERT INTO teacher (id, github_token)
            VALUES (:id, :github_token)
            """,
        )
            .bind("id", id)
            .bind("github_token", teacher.githubToken)
            .execute()
        return id
    }

    override fun getStudent(studentId: Int): Student? {
        return handle.createQuery(
            """
            SELECT name, email, Users.id, github_username, github_id, is_created, school_id,token FROM Student
            JOIN Users on Users.id = Student.id WHERE Student.id = :id
            """,
        )
            .bind("id", studentId)
            .mapTo<Student>()
            .firstOrNull()
    }

    override fun getTeacher(teacherId: Int): Teacher? {
        return handle.createQuery(
            """
            SELECT name, email, Users.id, github_username, github_id, is_created, github_token,token FROM Teacher
            JOIN Users on Users.id = Teacher.id WHERE Teacher.id = :id
            """,
        )
            .bind("id", teacherId)
            .mapTo<Teacher>()
            .firstOrNull()
    }

    override fun updateUserStatus(id: Int) {
        handle.createUpdate(
            """
            UPDATE Users
            SET is_created = true
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .execute()
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

        handle.createUpdate(
            """
            DELETE FROM users
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

    override fun getTeacherGithubToken(id: Int): String? {
        return handle.createQuery(
            """
            SELECT github_token FROM teacher
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo<String>()
            .firstOrNull()
    }
}
private fun helper(handle: Handle, id: Int): User? {
    return handle.createQuery(
        """
            SELECT name, email, Users.id, github_username, github_id, is_created, school_id,token FROM Student
            JOIN Users on Users.id = student.id
            WHERE Users.id = :id
            """,
    )
        .bind("id", id)
        .mapTo<Student>()
        .firstOrNull() ?: handle.createQuery(
        """
            SELECT name, email, Users.id, github_username, github_id, is_created,token FROM Teacher
            JOIN Users on Users.id = teacher.id
            WHERE Users.id = :id
            """,
    )
        .bind("id", id)
        .mapTo<Teacher>()
        .firstOrNull()
}
