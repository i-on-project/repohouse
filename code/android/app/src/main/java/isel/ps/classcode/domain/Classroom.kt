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
    val inviteCode: String,
    val isArchived: Boolean,
    val courseId: Int,
) {
    constructor(classCodeClassroomDeserialization: ClassCodeClassroomDeserialization) : this(
        id = classCodeClassroomDeserialization.id,
        name = classCodeClassroomDeserialization.name,
        lastSync = classCodeClassroomDeserialization.lastSync,
        inviteCode = classCodeClassroomDeserialization.inviteCode,
        isArchived = classCodeClassroomDeserialization.isArchived,
        courseId = classCodeClassroomDeserialization.courseId,
    )

    /**
     * Function to pass a classroom to a local classroom dto.
     */
    fun toLocalClassroomDto(courseName: String): LocalClassroomDto = LocalClassroomDto(
        id = id,
        name = name,
        lastSync = lastSync,
        inviteLink = inviteCode,
        isArchived = isArchived,
        courseId = courseId,
        courseName = courseName,
    )
}
