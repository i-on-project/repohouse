package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeClassroomDeserialization
import isel.ps.classcode.domain.dto.LocalClassroomDto
import java.sql.Timestamp


/**
 * The class that represents a classroom.
 */
data class Classroom(
    val id: Int,
    val name: String,
    val lastSync: Timestamp,
    val inviteLink: String,
    val isArchived: Boolean,
    val courseId: Int
) {
    constructor(classCodeClassroomDeserialization: ClassCodeClassroomDeserialization): this(
        id = classCodeClassroomDeserialization.id,
        name = classCodeClassroomDeserialization.name,
        lastSync = classCodeClassroomDeserialization.lastSync,
        inviteLink = classCodeClassroomDeserialization.inviteLink,
        isArchived = classCodeClassroomDeserialization.isArchived,
        courseId = classCodeClassroomDeserialization.courseId
    )

    /**
     * Function to pass a classroom to a local classroom dto.
     */
    fun toLocalClassroomDto(): LocalClassroomDto = LocalClassroomDto(
        id = id,
        name = name,
        lastSync = lastSync,
        inviteLink = inviteLink,
        isArchived = isArchived,
        courseId = courseId
    )
}
