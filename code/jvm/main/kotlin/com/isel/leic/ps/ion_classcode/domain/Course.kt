package com.isel.leic.ps.ion_classcode.domain


/**
 * Course Domain Interface
 */
data class Course(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val orgId: Long,
    val teachers: List<TeacherWithoutToken>,
    val isArchived: Boolean = false
)

/**
 * Course with Classrooms included Domain Interface
 */
data class CourseWithClassrooms(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val orgId: Long,
    val teachers: List<TeacherWithoutToken>,
    val isArchived: Boolean = false,
    val students: List<Student> = emptyList(),
    val classrooms: List<Classroom> = emptyList()
)
