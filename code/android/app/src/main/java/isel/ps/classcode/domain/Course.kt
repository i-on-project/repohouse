package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeCoursesDeserialization
import isel.ps.classcode.domain.dto.LocalCourseDto

/**
 * Represents a course
 */
data class Course(
    val id: Int,
    val orgUrl: String,
    val orgId: Long,
    val name: String,
) {
    constructor(classCodeCoursesDeserialization: ClassCodeCoursesDeserialization) : this(
        id = classCodeCoursesDeserialization.id,
        orgUrl = classCodeCoursesDeserialization.orgUrl,
        orgId = classCodeCoursesDeserialization.orgId,
        name = classCodeCoursesDeserialization.name
    )
    fun toLocalCourseDto(): LocalCourseDto = LocalCourseDto(id = id, name = name, orgUrl = orgUrl, orgId = orgId)
}
