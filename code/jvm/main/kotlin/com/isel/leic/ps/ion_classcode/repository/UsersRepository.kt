package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput

interface UsersRepository {
    fun createStudent(student: StudentInput): Int
    fun createPendingStudent(student: StudentInput): Int
    fun updateStudentSchool(userId: Int, schoolId: Int)
    fun createTeacher(teacher: TeacherInput): Int
    fun getAllStudents(): List<Student>
    fun getAllTeachers(): List<Teacher>
    fun getUserById(id: Int): User?
    fun getUserByEmail(email: String): User?
    fun getUserByToken(token: String): User?
    fun getUserByGithubId(githubId: Long): User?
    fun getStudentSchoolId(id: Int): Int?
    fun updateStudentStatus(id: Int)
    fun deleteStudent(id: Int)
    fun deleteTeacher(id: Int)
}
