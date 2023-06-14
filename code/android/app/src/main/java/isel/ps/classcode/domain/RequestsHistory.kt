package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeRequestsHistoryDeserialization

/**
 * Represents a error response body from ClassCode api
 */

data class RequestsHistory(
    val createTeamComposite: CreateTeamComposite,
    val joinTeam: List<JoinTeam>,
    val leaveTeam: List<LeaveTeamWithRepoName>,
    val archiveRepo: ArchiveRepo?,
) {
    constructor(deserialization: ClassCodeRequestsHistoryDeserialization) : this (
        createTeamComposite = CreateTeamComposite(deserialization = deserialization.createTeamComposite),
        joinTeam = deserialization.joinTeam.map { JoinTeam(deserialization = it) },
        leaveTeam = deserialization.leaveTeam.map { LeaveTeamWithRepoName(deserialization = it) },
        archiveRepo = deserialization.archiveRepo?.let { ArchiveRepo(deserialization = it) },
    )

    val size = joinTeam.size + leaveTeam.size + (if (archiveRepo != null) 1 else 0) + 1
}
