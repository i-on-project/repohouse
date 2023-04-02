package com.isel.leic.ps.ion_classcode.domain

data class Course(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val teacherId: Int,
)

data class CourseWithClassrooms(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val teacherId: Int,
    val classrooms: List<Classroom> = emptyList()
)

data class CourseWithStudents(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val teacherId: Int,
    val students: List<Student> = emptyList()
)
