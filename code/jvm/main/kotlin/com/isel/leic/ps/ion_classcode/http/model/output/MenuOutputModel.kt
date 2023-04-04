package com.isel.leic.ps.ion_classcode.http.model.output

data class MenuStudentOutputModel(
    val name: String,
    val schoolNumber: Int,
    val email: String,
    val courses: List<CourseOutputModel>
): OutputModel

data class MenuTeacherOutputModel(
    val name: String,
    val email: String,
    val courses: List<CourseOutputModel>
):OutputModel
