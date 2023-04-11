package com.isel.leic.ps.ion_classcode.http.model.input

/**
 * Represents a Course Input Model.
 */
data class CourseInputModel(
    val orgUrl: String,
    val name: String,
    val teacherId: Int
) {
    fun isNotValid(): Boolean {
        return !(orgUrl.isNotEmpty() && name.isNotEmpty() && teacherId > 0)
    }
}
