package com.isel.leic.ps.ion_classcode.http.model.output

import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.CourseWithClassrooms
import com.isel.leic.ps.ion_classcode.domain.Student

data class CourseOutputModel(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val teacherId: Int,
)

data class CourseWithClassroomOutputModel(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val teacherId: Int,
    val classrooms: List<Classroom>
)

data class CourseWithStudentsOutputModel(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val teacherId: Int,
    val students: List<Student>
)

data class CoursesOutputModel(
    val courses: List<CourseWithClassrooms>
)

data class CourseCreatedOutputModel(
    val course: Course,
)

data class EnterCourseOutputModel(
    val course: Course,
)

data class LeaveCourseOutputModel(
    val course: Course,
)
