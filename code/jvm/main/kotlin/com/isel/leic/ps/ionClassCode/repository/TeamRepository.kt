package com.isel.leic.ps.ionClassCode.repository

import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.Team
import com.isel.leic.ps.ionClassCode.domain.input.TeamInput

/**
 * Repository functions for Team Repository
 */
interface TeamRepository {
    fun createTeam(team: TeamInput): Team
    fun updateTeamCreatedStatus(id: Int)
    fun updateTeamClosedStatus(id: Int)
    fun getTeamById(id: Int): Team?
    fun getStudentsFromTeam(teamId: Int): List<Student>
    fun enterTeam(teamId: Int, studentId: Int)
    fun leaveTeam(teamId: Int, studentId: Int)
    fun getTeamsFromClassroom(classroomId: Int): List<Team>
    fun deleteTeam(teamId: Int)
    fun getTeamsFromAssignment(assignmentId: Int): List<Team>
    fun getTeamsFromStudent(studentId: Int): List<Team>
}
