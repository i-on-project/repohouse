package com.isel.leic.ps.ionClassCode.http.model.output

import com.isel.leic.ps.ionClassCode.domain.Assignment
import com.isel.leic.ps.ionClassCode.domain.Feedback
import com.isel.leic.ps.ionClassCode.domain.Repo
import com.isel.leic.ps.ionClassCode.domain.StudentWithoutToken
import com.isel.leic.ps.ionClassCode.domain.Team
import com.isel.leic.ps.ionClassCode.domain.requests.ArchiveRepo
import com.isel.leic.ps.ionClassCode.domain.requests.JoinTeam
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveTeam

/**
 * Represents a Team Output Model.
 */
data class TeamOutputModel(
    val team: Team,
    val students: List<StudentWithoutToken>,
    val repo: Repo?,
    val feedbacks: List<Feedback>,
    val assignment: Assignment
)

/**
 * Represents a Teams Output Model.
 */
data class TeamsOutputModel(
    val teams: List<TeamModel>,
)

/**
 * Represents a Team Closed Output Model.
 */
data class TeamClosedOutputModel(
    val closed: Boolean,
)

/**
 * Represents a Team Model for inner functions.
 */
data class TeamModel(
    val team: Team,
    val students: List<StudentWithoutToken>,
    val repo: Repo?,
    val feedbacks: List<Feedback>,
    val assignment: Assignment
)

/**
 * Represents a Team Requests Output Model.
 */
data class TeamRequestsModel(
    val team: Team,
    val joinTeam: List<JoinTeam>,
    val leaveTeam: List<LeaveTeam>,
)

data class RequestsThatNeedApproval(
    val joinTeam: List<JoinTeam>,
    val leaveTeam: List<LeaveTeam>,
)

data class RequestsHistory(
    val createTeamComposite: CreateTeamComposite,
    val joinTeam: List<JoinTeam>,
    val leaveTeam: List<LeaveTeam>,
    val archiveRepo: ArchiveRepo?,
)

data class TeamRequestsForMobileModel(
    val needApproval: RequestsThatNeedApproval,
    val requestsHistory: RequestsHistory,
)

/**
 * Represents an Update Create Team Status Output.
 */
data class UpdateCreateTeamStatusOutput(
    val result: Boolean,
)
