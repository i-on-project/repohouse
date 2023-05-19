package com.isel.leic.ps.ionClassCode.repository

import com.isel.leic.ps.ionClassCode.domain.input.TeamInput
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiTeamRepository
import com.isel.leic.ps.ionClassCode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class TeamRepositoryTests {

    @Test
    fun `can create a team`() = testWithHandleAndRollback { handle ->
        val teamRepo = JdbiTeamRepository(handle = handle)
        val assignmentId = 1
        val created = teamRepo.createTeam(team = TeamInput(name = "test12", assignmentId = assignmentId, isCreated = false))
        val team = teamRepo.getTeamById(id = created.id)
        assert(team != null)
    }

    @Test
    fun `can get a team by id`() = testWithHandleAndRollback { handle ->
        val teamRepo = JdbiTeamRepository(handle = handle)
        val teamId = 1
        val teamName = "team1"
        val team = teamRepo.getTeamById(id = teamId) ?: fail("Team not found")
        assert(team.name == teamName)
    }

    @Test
    fun `can update team status`() = testWithHandleAndRollback { handle ->
        val teamRepo = JdbiTeamRepository(handle = handle)
        val teamId = 1
        teamRepo.updateTeamStatus(id = teamId)
        val team = teamRepo.getTeamById(id = teamId) ?: fail("Team not found")
        assert(team.isCreated)
    }

    @Test
    fun `can get teams from a assignment`() = testWithHandleAndRollback { handle ->
        val teamRepo = JdbiTeamRepository(handle = handle)
        val assignmentId = 1
        val teams = teamRepo.getTeamsFromAssignment(assignmentId = assignmentId)
        assert(teams.size == 2)
    }

    @Test
    fun `can enter a team`() = testWithHandleAndRollback { handle ->
        val teamRepo = JdbiTeamRepository(handle = handle)
        val teamId = 2
        val studentId = 5
        teamRepo.enterTeam(teamId = teamId, studentId = studentId)
        val students = teamRepo.getStudentsFromTeam(teamId = teamId)
        assert(students.any { it.id == studentId })
    }

    @Test
    fun `can get teams from a student`() = testWithHandleAndRollback { handle ->
        val teamRepo = JdbiTeamRepository(handle = handle)
        val studentId = 4
        val teams = teamRepo.getTeamsFromStudent(studentId = studentId)
        assert(teams.size == 2)
    }

    @Test
    fun `can a student leave a team`() = testWithHandleAndRollback { handle ->
        val teamRepo = JdbiTeamRepository(handle = handle)
        val teamId = 1
        val studentId = 5
        teamRepo.leaveTeam(teamId = teamId, studentId = studentId)
        val teams = teamRepo.getTeamsFromStudent(studentId = studentId)
        assert(teams.isEmpty())
    }

    @Test
    fun `can delete a team`() = testWithHandleAndRollback { handle ->
        val teamRepo = JdbiTeamRepository(handle = handle)
        val teamId = 3
        teamRepo.deleteTeam(teamId = teamId)
        val team = teamRepo.getTeamById(id = teamId)
        assert(team == null)
    }

    @Test
    fun `cannot delete a team`() = testWithHandleAndRollback { handle ->
        val teamRepo = JdbiTeamRepository(handle = handle)
        val teamId = 1
        try {
            teamRepo.deleteTeam(teamId = teamId)
            fail("Should not be able to delete a team")
        } catch (e: Exception) {
            assert(true)
        }
    }

    @Test
    fun `can get students of a team`() = testWithHandleAndRollback { handle ->
        val teamRepo = JdbiTeamRepository(handle = handle)
        val teamId = 1
        val students = teamRepo.getStudentsFromTeam(teamId = teamId)
        assert(students.size == 2)
    }
}
