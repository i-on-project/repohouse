package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeCreateTeamDeserialization

/**
 * Represents a ClassCode Create Team
 */

data class CreateTeam(
    val requestId: Int,
    val creator: Int,
    val state: String = "Pending",
    val composite: Int,
    val teamId: Int,
    val teamName: String,
    val gitHubTeamId: Int?,
) {
    constructor(deserialization: ClassCodeCreateTeamDeserialization) : this(
        requestId = deserialization.requestId,
        creator = deserialization.creator,
        state = deserialization.state,
        composite = deserialization.composite,
        teamId = deserialization.teamId,
        teamName = deserialization.teamName,
        gitHubTeamId = deserialization.gitHubTeamId,
    )
}
