package com.isel.leic.ps.ion_classcode.http.model.output


data class TeachersPendingOutputModel(
    val teachers : List<TeacherPending>
):OutputModel


data class TeacherPending(
    val name: String,
    val email: String,
    val id: Int,
    val requestId: Int
)