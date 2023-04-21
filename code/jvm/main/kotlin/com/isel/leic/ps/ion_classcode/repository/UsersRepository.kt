package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.PendingStudent
import com.isel.leic.ps.ion_classcode.domain.PendingTeacher
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput

/**
 * Repository functions for User Repository
 */
interface UsersRepository {
    fun checkIfEmailExists(email: String): Boolean
    fun checkIfGithubUsernameExists(githubUsername: String): Boolean
    fun checkIfGithubIdExists(githubId: Long): Boolean
    fun checkIfTokenExists(token: String): Boolean
    fun checkIfGithubTokenExists(githubToken: String): Boolean
    fun checkIfSchoolIdExists(schoolId: Int): Boolean
    fun createPendingStudent(student: StudentInput): PendingStudent
    fun createStudent(student: StudentInput): Student?
    fun createPendingTeacher(teacher: TeacherInput): PendingTeacher
    fun createTeacher(teacher: TeacherInput): Teacher?
    fun updateStudentSchoolId(userId: Int, schoolId: Int)
    fun getStudent(studentId: Int): Student?
    fun getTeacher(teacherId: Int): Teacher?
    fun getAllStudents(): List<Student>
    fun getAllTeachers(): List<Teacher>
    fun getUserById(id: Int): User?
    fun getUserByEmail(email: String): User?
    fun getUserByToken(token: String): User?
    fun getUserByGithubId(githubId: Long): User?
    fun getPendingUserByGithubId(githubId: Long): User?
    fun getStudentSchoolId(id: Int): Int?
    fun updateUserStatus(id: Int)
    fun deleteStudent(id: Int)
    fun deleteTeacher(id: Int)
    fun getTeacherGithubToken(id: Int): String?
    fun deletePendingUsers()
    fun updateTeacherGithubToken(id: Int, token: String)
}
