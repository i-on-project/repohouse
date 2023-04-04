package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput

interface CourseRepository {
    fun createCourse(course: CourseInput): Course
    fun deleteCourse(courseId: Int)
    fun enterCourse(courseId: Int, studentId: Int): Course
    fun leaveCourse(courseId: Int, studentId: Int): Course
    fun archiveCourse(courseId: Int)
    fun getCourseClassrooms(courseId: Int): List<Classroom>
    fun getAllTeacherCourses(teacherId: Int): List<Course>
    fun getAllStudentCourses(studentId: Int): List<Course>
    fun getStudentInCourse(courseId: Int): List<Student>
    fun getCourse(courseId: Int): Course?
    fun getCourseByOrg(orgUrl: String): Course?
    fun getCourseByName(name: String): Course?
    fun isUserInCourse(userId: Int, courseId: Int): Boolean
}
