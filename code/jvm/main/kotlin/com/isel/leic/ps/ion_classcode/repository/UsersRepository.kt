package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User

interface UsersRepository {
    fun createStudent(student: Student): Int
    fun getStudentById(id: Int): Student?
    fun getStudentByEmail(email: String): Student?
    fun getUserByToken(token: String): User?
    fun createTeacher(teacher: Teacher): Int
    fun getTeacherById(id: Int): Teacher?
    fun getTeacherByEmail(email: String): Teacher?
    fun deleteStudent(id: Int)
    fun deleteTeacher(id: Int)


}
