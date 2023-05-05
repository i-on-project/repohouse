package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.TeacherWithoutToken
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput

/**
 * Repository functions for Course Repository
 */
interface CourseRepository {
    fun checkIfOrgUrlExists(orgUrl: String): Boolean
    fun checkIfCourseNameExists(name: String): Boolean
    fun createCourse(course: CourseInput): Course
    fun deleteCourse(courseId: Int)
    fun leaveCourse(courseId: Int, studentId: Int): Course
    fun archiveCourse(courseId: Int)
    fun addTeacherToCourse(teacherId: Int, courseId: Int): Course
    fun getCourseTeachers(courseId: Int): List<TeacherWithoutToken>
    fun getCourseAllClassrooms(courseId: Int): List<Classroom>
    fun getCourseUserClassrooms(courseId: Int, userId: Int, student: Boolean): List<Classroom>
    fun getAllUserCourses(userId: Int): List<Course>
    fun getAllStudentCourses(studentId: Int): List<Course>
    fun getStudentInCourse(courseId: Int): List<Student>
    fun getCourse(courseId: Int): Course?
    fun getCourseByOrg(orgUrl: String): Course?
    fun getCourseByName(name: String): Course?
    fun isStudentInCourse(studentId: Int, courseId: Int): Boolean
}
