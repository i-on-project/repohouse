package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeTeamDeserialization

/**
 * Represents a team
 */
data class Team (
    val id: Int,
    val name: String,
    val isCreated: Boolean,
    val assignment: Int
) {
    constructor(classCodeTeamDeserialization: ClassCodeTeamDeserialization) : this(
        classCodeTeamDeserialization.id,
        classCodeTeamDeserialization.name,
        classCodeTeamDeserialization.isCreated,
        classCodeTeamDeserialization.assignment
    )
}
