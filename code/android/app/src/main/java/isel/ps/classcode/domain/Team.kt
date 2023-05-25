package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeTeamDeserialization
import isel.ps.classcode.domain.dto.LocalTeamDto

/**
 * Represents a team
 */
data class Team(
    val id: Int,
    val name: String,
    val isCreated: Boolean,
    val assignment: Int
) {
    constructor(classCodeTeamDeserialization: ClassCodeTeamDeserialization) : this(
        classCodeTeamDeserialization.id,
        classCodeTeamDeserialization.name,
        classCodeTeamDeserialization.isCreated,
        classCodeTeamDeserialization.assignment,
    )
    val teamSlug = name.replace(" ", "-").lowercase()
    fun toLocalTeamDto(courseId: Int, courseName: String, classroomId: Int): LocalTeamDto = LocalTeamDto(id = id, name = name, isCreated = isCreated, assignment = assignment, courseId = courseId, courseName = courseName, classroomId = classroomId)
}
