package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.input.CourseInput

interface CourseRepository {
    fun createCourse(course: CourseInput): Int
    fun deleteCourse(courseId: Int)
    fun enterCourse(courseId: Int, studentId: Int)
    fun leaveCourse(courseId: Int, studentId: Int)
}