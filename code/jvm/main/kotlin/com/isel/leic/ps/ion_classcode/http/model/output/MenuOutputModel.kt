package com.isel.leic.ps.ion_classcode.http.model.output

/**
 * Represents a Menu Student Output Model.
 */
data class MenuStudentOutputModel(
    val name: String,
    val schoolNumber: Int,
    val email: String,
    val courses: List<CourseOutputModel>
): OutputModel

/**
 * Represents a Menu Teacher Output Model.
 */
data class MenuTeacherOutputModel(
    val name: String,
    val email: String,
    val courses: List<CourseOutputModel>
):OutputModel
