package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Team
import com.isel.leic.ps.ion_classcode.domain.input.TeamInput

interface TeamRepository {
    fun createTeam(team: TeamInput)
    fun getTeamById(id: Int): Team?
    fun leaveTeam(teamId: Int, userId: Int)
}
