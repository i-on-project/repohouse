package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.repository.UsersRepository
import org.jdbi.v3.core.Handle

class JdbiUsersRepository(
    private val handle: Handle,
) : UsersRepository {
    override fun createStudent(student: Student): Int {
        val id = handle.createUpdate(
            """
            INSERT INTO User (name, email, github_username)
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
            INSERT INTO Student (id, school_id)
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
            SELECT * FROM Student
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo(Student::class.java)
            .firstOrNull()
    }

    override fun createTeacher(teacher: Teacher): Int {
        val id = handle.createUpdate(
            """
            INSERT INTO User (name, email, github_username)
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
            SELECT * FROM Teacher
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo(Teacher::class.java)
            .firstOrNull()
    }
}
