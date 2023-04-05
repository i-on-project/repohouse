package com.isel.leic.ps.ion_classcode.http.model.output

import com.isel.leic.ps.ion_classcode.domain.Feedback
import com.isel.leic.ps.ion_classcode.domain.Repo
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Team
import com.isel.leic.ps.ion_classcode.domain.requests.CreateTeam
import com.isel.leic.ps.ion_classcode.domain.requests.JoinTeam
import com.isel.leic.ps.ion_classcode.domain.requests.LeaveTeam

data class TeamOutputModel (
    val team: Team,
    val students: List<Student>,
    val repos: List<Repo>,
    val feedbacks: List<Feedback>
)

data class TeamRequestsOutputModel (
    val team: Team,
    val createTeam: List<CreateTeam>,
    val joinTeam: List<JoinTeam>,
    val leaveTeam: List<LeaveTeam>
)

