package com.isel.leic.ps.ion_classcode.http.model.input

data class TeachersPendingInputModel(
    val approved: List<Int>,
    val rejected: List<Int>,
)
