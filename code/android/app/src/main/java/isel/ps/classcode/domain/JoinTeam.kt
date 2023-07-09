package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeJoinTeamDeserialization

/**
 * Represents a ClassCode Join Team
 */
data class JoinTeam(
    val requestId: Int,
    val creator: Int,
    val state: String,
    val composite: Int?,
    val teamId: Int,
    val githubUsername: String,
) {
    constructor(deserialization: ClassCodeJoinTeamDeserialization) : this(
        requestId = deserialization.requestId,
        creator = deserialization.creator,
        state = deserialization.state,
        composite = deserialization.composite,
        teamId = deserialization.teamId,
        githubUsername = deserialization.githubUsername,
    )
}
