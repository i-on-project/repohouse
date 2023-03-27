package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput

interface UsersRepository {
    fun createStudent(student: StudentInput): Int
    fun createTeacher(teacher: TeacherInput): Int
    fun getUserById(id: Int): User?
    fun getUserByEmail(email: String): User?
    fun getUserByToken(token: String): User?
    fun getUserByGithubId(githubId: Int): User?
    fun deleteStudent(id: Int)
    fun deleteTeacher(id: Int)
}
