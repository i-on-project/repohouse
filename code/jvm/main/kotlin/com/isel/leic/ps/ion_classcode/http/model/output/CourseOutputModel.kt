package com.isel.leic.ps.ion_classcode.http.model.output

import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.Teacher

/**
 * Represents a Course Output Model.
 */
data class CourseOutputModel(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val teacher: List<Teacher>,
) : OutputModel

/**
 * Represents a Course with the respective classrooms included Output Model.
 */
data class CourseWithClassroomOutputModel(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val teacher: List<Teacher>,
    val isArchived: Boolean,
    val classrooms: List<Classroom>,
) : OutputModel

/**
 * Represents a Course Created Output Model.
 */
data class CourseCreatedOutputModel(
    val course: Course,
)

/**
 * Represents a Course Deleted Output Model.
 */
data class CourseDeletedOutputModel(
    val id: Int,
    val deleted: Boolean
) : OutputModel
