package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeLeaveTeamDeserialization

/**
 * Represents a leave team request
 */

data class LeaveTeam(
    val requestId: Int,
    val creator: Int,
    val state: String,
    val composite: Int?,
    val teamId: Int,
    val githubUsername: String,
    val membersCount: Int,
    val teamName: String
) {
    constructor(deserialization: ClassCodeLeaveTeamDeserialization) : this (
        requestId = deserialization.requestId,
        creator = deserialization.creator,
        state = deserialization.state,
        composite = deserialization.composite,
        teamId = deserialization.teamId,
        githubUsername = deserialization.githubUsername,
        membersCount = deserialization.membersCount,
        teamName = deserialization.teamName,
    )
}
