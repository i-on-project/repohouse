package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher

interface UsersRepository {
    fun createStudent(student: Student): Int
    fun getStudentById(id: Int): Student?
    fun createTeacher(teacher: Teacher): Int
    fun getTeacherById(id: Int): Teacher?
}
