package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput

interface CourseRepository {
    fun createCourse(course: CourseInput): Int //
    fun deleteCourse(courseId: Int) //
    fun enterCourse(courseId: Int, studentId: Int) //
    fun leaveCourse(courseId: Int, studentId: Int) //
    fun getCourseClassrooms(courseId: Int): List<Classroom>
    fun getAllTeacherCourses(teacherId: Int): List<Course>
    fun getAllStudentCourses(studentId: Int): List<Course>
    fun getStudentInCourse(courseId: Int): List<Student>
    fun getCourse(courseId: Int): Course? //
}
