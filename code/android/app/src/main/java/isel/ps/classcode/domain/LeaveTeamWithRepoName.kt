package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeLeaveTeamWithRepoNameDeserialization

/**
 * Represents a leave team request
 */

data class LeaveTeamWithRepoName(
    val leaveTeam: LeaveTeam,
    val repoName: String,
) {
    constructor(deserialization: ClassCodeLeaveTeamWithRepoNameDeserialization) : this (
        leaveTeam = LeaveTeam(deserialization = deserialization.leaveTeam),
        repoName = deserialization.repoName,
    )
}
