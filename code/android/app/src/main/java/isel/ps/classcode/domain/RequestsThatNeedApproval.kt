package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeRequestsThatNeedApprovalDeserialization

/**
 * Represents a error response body from ClassCode api
 */

data class RequestsThatNeedApproval(
    val joinTeam: List<JoinTeam>,
    val leaveTeam: List<LeaveTeamWithRepoName>,
) {
    constructor(deserialization: ClassCodeRequestsThatNeedApprovalDeserialization) : this (
        joinTeam = deserialization.joinTeam.map { JoinTeam(deserialization = it) },
        leaveTeam = deserialization.leaveTeam.map { LeaveTeamWithRepoName(deserialization = it) },
    )
    val size = joinTeam.size + leaveTeam.size
}
