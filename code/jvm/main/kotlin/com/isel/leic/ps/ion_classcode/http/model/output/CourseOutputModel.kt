package com.isel.leic.ps.ion_classcode.http.model.output

import com.isel.leic.ps.ion_classcode.domain.Classroom

data class CourseOutputModel(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val teacherId: Int,
    val classrooms: List<Classroom>
)

data class CourseCreatedOutputModel(
    val created: Boolean = true,
    val id: Int
)

