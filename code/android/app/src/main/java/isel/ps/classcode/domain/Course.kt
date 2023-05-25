package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeCourseWithoutClassroomsDeserialization
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
    constructor(classCodeCourseWithoutClassroomsDeserialization: ClassCodeCourseWithoutClassroomsDeserialization) : this(
        id = classCodeCourseWithoutClassroomsDeserialization.id,
        orgUrl = classCodeCourseWithoutClassroomsDeserialization.orgUrl,
        orgId = classCodeCourseWithoutClassroomsDeserialization.orgId,
        name = classCodeCourseWithoutClassroomsDeserialization.name,
    )

    /**
     * Function to pass a course to a local course dto.
     */
    fun toLocalCourseDto(): LocalCourseDto = LocalCourseDto(id = id, name = name, orgUrl = orgUrl, orgId = orgId)
}
