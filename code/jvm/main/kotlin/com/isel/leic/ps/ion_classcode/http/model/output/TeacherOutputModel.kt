package com.isel.leic.ps.ion_classcode.http.model.output

/**
 * Represents a Teachers Pending Output Model.
 */
data class TeachersPendingOutputModel(
    val teachers: List<TeacherPending>
) : OutputModel

/**
 * Represents a Teacher Pending Model for inner functions.
 */
data class TeacherPending(
    val name: String,
    val email: String,
    val id: Int,
    val applyRequestId: Int
)
