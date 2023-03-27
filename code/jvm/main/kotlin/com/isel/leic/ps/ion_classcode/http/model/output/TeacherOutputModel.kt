package com.isel.leic.ps.ion_classcode.http.model.output

data class TeacherOutputModel(
    val id: Int,
    val name: String,
    val email: String
)

data class TeachersOutputModel(
    val teachers : List<TeacherOutputModel>
)
