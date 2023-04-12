package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Team
import com.isel.leic.ps.ion_classcode.domain.input.TeamInput

/**
 * Repository functions for Team Repository
 */
interface TeamRepository {
    fun createTeam(team: TeamInput): Int
    fun updateTeamStatus(id: Int, status: Boolean)
    fun getTeamById(id: Int): Team?
    fun getStudentsFromTeam(teamId: Int): List<Student>
    fun enterTeam(teamId: Int, studentId: Int)
    fun leaveTeam(teamId: Int, studentId: Int)
    fun deleteTeam(teamId: Int)
    fun getTeamsFromAssignment(assignmentId: Int): List<Team>
    fun getTeamsFromStudent(studentId: Int): List<Team>
}
