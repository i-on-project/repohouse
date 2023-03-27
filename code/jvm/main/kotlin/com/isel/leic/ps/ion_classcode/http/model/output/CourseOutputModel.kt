package com.isel.leic.ps.ion_classcode.http.model.output

data class CourseOutputModel(
    val id: Int,
    val name: String,
    val acronym: String,
    val year: Int,
    val semester: Int,
    val teacher: String,
    val students: List<String>
)
