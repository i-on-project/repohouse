package com.isel.leic.ps.ionClassCode.http.model.output

import com.isel.leic.ps.ionClassCode.domain.Feedback
import com.isel.leic.ps.ionClassCode.domain.Repo
import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.Team
import com.isel.leic.ps.ionClassCode.domain.requests.JoinTeam
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveTeam

/**
 * Represents a Team Output Model.
 */
data class TeamOutputModel(
    val team: Team,
    val students: List<Student>,
    val repos: List<Repo>,
    val feedbacks: List<Feedback>,
)

/**
 * Represents a Teams Output Model.
 */
data class TeamsOutputModel(
    val teams: List<TeamModel>,
)

/**
 * Represents a Team Model for inner functions.
 */
data class TeamModel(
    val team: Team,
    val students: List<Student>,
    val repos: List<Repo>,
    val feedbacks: List<Feedback>,
)

/**
 * Represents a Team Requests Output Model.
 */
data class TeamRequestsModel(
    val team: Team,
    val joinTeam: List<JoinTeam>,
    val leaveTeam: List<LeaveTeam>,
)
