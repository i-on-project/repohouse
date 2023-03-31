package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Team
import com.isel.leic.ps.ion_classcode.domain.input.TeamInput

interface TeamRepository {
    fun createTeam(team: TeamInput): Int
    fun updateTeamStatus(id: Int, status: Boolean)
    fun getTeamById(id: Int): Team?
    fun enterTeam(teamId: Int, studentId: Int)
    fun leaveTeam(teamId: Int, studentId: Int)
    fun deleteTeam(teamId: Int)
    fun getTeamsFromAssignment(assignmentId: Int): List<Team>
    fun getTeamsFromStudent(studentId: Int): List<Team>
}
