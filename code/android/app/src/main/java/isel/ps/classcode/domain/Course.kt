package isel.ps.classcode.domain

import isel.ps.classcode.domain.dto.LocalCourseDto

/**
 * Represents a course
 */
data class Course(
    val id: Int,
    val imageUrl: String,
    val name: String,
) {
    fun toLocalCourseDto(): LocalCourseDto = LocalCourseDto(id = id, name = name, imageUrl = imageUrl)
}
