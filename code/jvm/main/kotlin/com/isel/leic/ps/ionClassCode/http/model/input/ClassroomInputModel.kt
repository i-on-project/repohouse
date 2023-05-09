package com.isel.leic.ps.ionClassCode.http.model.input

/**
 * Represents a Classroom Input Model.
 */
data class ClassroomInputModel(
    val name: String,
)

/**
 * Represents a Classroom Update Input Model.
 */
data class ClassroomUpdateInputModel(
    val name: String,
) {
    fun isNotValid() = name.isBlank()
}
