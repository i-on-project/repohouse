package com.isel.leic.ps.ionClassCode.http.model.input

/**
 * Represents a Course Input Model.
 */
data class CourseInputModel(
    val orgUrl: String,
    val name: String,
    val orgId: Long
) {
    fun isNotValid(): Boolean {
        return !(orgUrl.isNotEmpty() && name.isNotEmpty() && orgId > 0)
    }
}
