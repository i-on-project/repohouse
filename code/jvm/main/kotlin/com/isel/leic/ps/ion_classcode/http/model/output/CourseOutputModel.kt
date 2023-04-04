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
) : OutputModel

data class CourseWithClassroomOutputModel(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val teacherId: Int,
    val classrooms: List<Classroom>,
) : OutputModel

data class CourseWithStudentsOutputModel(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val teacherId: Int,
    val students: List<Student>,
) : OutputModel

data class CoursesOutputModel(
    val courses: List<CourseWithClassrooms>,
) : OutputModel

data class CourseCreatedOutputModel(
    val course: Course,
)

