package com.isel.leic.ps.ionClassCode.repository.jdbi

import com.isel.leic.ps.ionClassCode.domain.PendingStudent
import com.isel.leic.ps.ionClassCode.domain.PendingTeacher
import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.Teacher
import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.domain.input.StudentInput
import com.isel.leic.ps.ionClassCode.domain.input.TeacherInput
import com.isel.leic.ps.ionClassCode.repository.UsersRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.security.MessageDigest
import java.util.Base64

/**
 * Implementation of the User methods
 */
class JdbiUsersRepository(
    private val handle: Handle,
) : UsersRepository {

    /**
     * Method to verify if an email exists
     */
    override fun checkIfEmailExists(email: String): Boolean {
        return handle.createQuery(
            """
            SELECT id FROM Users
            WHERE email = :email
            """,
        )
            .bind("email", email)
            .mapTo<Int>()
            .firstOrNull() != null
    }

    /**
     * Method to verify if a GitHub username exists
     */
    override fun checkIfGithubUsernameExists(githubUsername: String): Boolean {
        return handle.createQuery(
            """
            SELECT id FROM Users
            WHERE github_username = :github_username
            """,
        )
            .bind("github_username", githubUsername)
            .mapTo<Int>()
            .firstOrNull() != null
    }

    /**
     * Method to verify if a GitHub id exists
     */
    override fun checkIfGithubIdExists(githubId: Long): Boolean {
        return handle.createQuery(
            """
            SELECT id FROM Users
            WHERE github_id = :github_id
            """,
        )
            .bind("github_id", githubId)
            .mapTo<Int>()
            .firstOrNull() != null
    }

    /**
     * Method to verify if a token exists
     */
    override fun checkIfTokenExists(token: String): Boolean {
        return handle.createQuery(
            """
            SELECT id FROM Users
            WHERE token = :token
            """,
        )
            .bind("token", token)
            .mapTo<Int>()
            .firstOrNull() != null
    }

    /**
     * Method to verify if a GitHub token exists
     */
    override fun checkIfGithubTokenExists(githubToken: String): Boolean {
        return handle.createQuery(
            """
            SELECT id FROM teacher
            WHERE github_token = :github_token
            """,
        )
            .bind("github_token", githubToken)
            .mapTo<Int>()
            .firstOrNull() != null
    }
    data class ChallengeInfo(
        val challenge: String,
        val challengeMethod: String,
    )
    override fun verifySecret(secret: String, state: String): Boolean {
        val query = handle.createQuery(
            """
            SELECT challenge, challenge_method FROM challengeinfo
            WHERE state = :state
            """,
        )
            .bind("state", state)
            .mapTo<ChallengeInfo>()
            .firstOrNull() ?: return false
        handle.createUpdate(
            """
            DELETE FROM challengeinfo
            WHERE state = :state
            """,
        )
            .bind("state", state)
            .execute()
        return if (query.challengeMethod == "plain") {
            secret == query.challenge
        } else {
            val secretBytes = secret.toByteArray()
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val digest = messageDigest.digest(secretBytes)
            val challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
            challenge == query.challenge
        }
    }

    /**
     * Method to verify if a School id exists
     */
    override fun checkIfSchoolIdExists(schoolId: Int): Boolean {
        return handle.createQuery(
            """
            SELECT id FROM student
            WHERE school_id = :school_id
            """,
        )
            .bind("school_id", schoolId)
            .mapTo<Int>()
            .firstOrNull() != null
    }

    /**
     * Method to create a pending student
     */
    override fun createPendingStudent(student: StudentInput): PendingStudent {
        val id = handle.createUpdate(
            """
            INSERT INTO pendingstudent (email, github_username, github_id, token, name,created_at)
            VALUES (:email, :github_username, :github_id, :token, :name,now())
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

        return PendingStudent(
            id = id,
            name = student.name,
            email = student.email,
            githubUsername = student.githubUsername,
            githubId = student.githubId,
            token = student.token,
        )
    }

    /**
     * Method to create a student
     */
    override fun createStudent(student: StudentInput): Student? {
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
        if (id < 0) return null
        handle.createUpdate(
            """
            INSERT INTO student (id, school_id)
            VALUES (:id, :schoolId)
            """,
        )
            .bind("id", id)
            .bind("schoolId", student.schoolId)
            .execute()

        return Student(
            id = id,
            name = student.name,
            email = student.email,
            githubUsername = student.githubUsername,
            githubId = student.githubId,
            token = student.token,
            schoolId = student.schoolId,
            isCreated = false,
        )
    }

    /**
     * Method to create a pending teacher
     */
    override fun createPendingTeacher(teacher: TeacherInput): PendingTeacher {
        val id = handle.createUpdate(
            """
            INSERT INTO pendingteacher (email, github_username, github_id, token, name,github_token,created_at)
            VALUES (:email, :github_username, :github_id, :token, :name,:github_token, now())
            RETURNING id
            """,
        )
            .bind("email", teacher.email)
            .bind("github_username", teacher.githubUsername)
            .bind("github_id", teacher.githubId)
            .bind("token", teacher.token)
            .bind("name", teacher.name)
            .bind("github_token", teacher.githubToken)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()

        return PendingTeacher(
            id = id,
            name = teacher.name,
            email = teacher.email,
            githubUsername = teacher.githubUsername,
            githubId = teacher.githubId,
            token = teacher.token,
            githubToken = teacher.githubToken,
        )
    }

    /**
     * Method to get all students
     */
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

    /**
     * Method to get all teachers
     */
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

    /**
     * Method to get a user by id
     */
    override fun getUserById(userId: Int): User? {
        return helper(handle = handle, id = userId)
    }

    /**
     * Method to get a user by email
     */
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

    /**
     * Method to get a user by is GitHub id
     */
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

    override fun getPendingTeacherByGithubId(githubId: Long): PendingTeacher? {
        return handle.createQuery(
            """
            SELECT  id,name,email,github_username,is_created,github_id,token,github_token FROM pendingteacher
            WHERE github_id = :github_id 
            Order By created_at DESC limit 1
            """,
        )
            .bind("github_id", githubId)
            .mapTo<PendingTeacher>()
            .firstOrNull()
    }

    override fun getPendingStudentByGithubId(githubId: Long): PendingStudent? {
        return handle.createQuery(
            """
            SELECT id,name,email,github_username,is_created,github_id,token FROM pendingstudent
            WHERE github_id = :github_id
            """,
        )
            .bind("github_id", githubId)
            .mapTo<PendingStudent>()
            .firstOrNull()
    }

    /**
     * Method to get a user by is token
     */
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

    /**
     * Method to store some auth info
     */

    override fun storeChallengeInfo(challengeMethod: String, challenge: String, state: String) {
        handle.createUpdate(
            """
               INSERT INTO challengeinfo (state, challenge, challenge_method) 
               values (:state, :challenge, :challengeMethod)
            """,
        )
            .bind("state", state)
            .bind("challengeMethod", challengeMethod)
            .bind("challenge", challenge)
            .execute()
    }

    /**
     * Method to get a student by is school id
     */
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

    /**
     * Method to update a student school id
     */
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

    /**
     * Method to create a teacher
     */
    override fun createTeacher(teacher: TeacherInput): Teacher? {
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
        if (id < 0) return null
        handle.createUpdate(
            """
            INSERT INTO teacher (id, github_token)
            VALUES (:id, :github_token)
            """,
        )
            .bind("id", id)
            .bind("github_token", teacher.githubToken)
            .execute()
        return Teacher(name = teacher.name, email = teacher.email, id = id, githubUsername = teacher.githubUsername, githubId = teacher.githubId, isCreated = true, token = teacher.githubToken)
    }

    /**
     * Method to get a student
     */
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

    /**
     * Method to get a teacher
     */
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

    /**
     * Method to update a user status
     */
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

    /**
     * Method to get a teacher GitHub token
     */
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

    /**
     * Method to delete all pending users that are older than 1 day
     */
    override fun deletePendingUsers() {
        handle.createUpdate(
            """
                DELETE FROM apply
                WHERE pending_teacher_id IN (SELECT id FROM pendingteacher
                WHERE created_at < now() - interval '1 day')
            """,
        ).execute()

        handle.createUpdate(
            """
            DELETE FROM pendingstudent
            WHERE  created_at < now() - interval '1 day'
            """,
        ).execute()

        handle.createUpdate(
            """
            DELETE FROM pendingteacher
            WHERE  created_at < now() - interval '1 day'
            """,
        ).execute()
    }

    override fun updateTeacherGithubToken(id: Int, token: String) {
        handle.createUpdate(
            """
            UPDATE Teacher
            SET github_token = :token
            WHERE id = :id
            """,
        )
            .bind("token", token)
            .bind("id", id)
            .execute()
    }
}

/**
 * Method to help get a user type
 * Can be a student or a teacher
 */
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
