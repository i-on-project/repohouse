package com.isel.leic.ps.ionClassCode.http.model.input

/**
 * Represents a Teacher Input Model.
 */
data class TeachersPendingInputModel(
    val approved: List<Int>,
    val rejected: List<Int>,
) {
    fun isNotValid(): Boolean {
        return approved.isEmpty() && rejected.isEmpty()
    }
}
