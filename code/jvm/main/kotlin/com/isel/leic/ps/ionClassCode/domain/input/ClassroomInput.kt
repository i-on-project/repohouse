package com.isel.leic.ps.ionClassCode.domain.input

/**
 * Classroom Input Interface
 */
data class ClassroomInput(
    val name: String,
    val courseId: Int,
    val teacherId: Int
) {
    fun isNotValid(): Boolean {
        return name.isBlank()
    }
}
