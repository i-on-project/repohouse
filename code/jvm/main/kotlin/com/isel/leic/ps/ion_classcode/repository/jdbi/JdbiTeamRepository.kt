package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Team
import com.isel.leic.ps.ion_classcode.domain.input.AssigmentInput
import com.isel.leic.ps.ion_classcode.domain.input.FeedbackInput
import com.isel.leic.ps.ion_classcode.domain.input.TeamInput
import com.isel.leic.ps.ion_classcode.repository.AssigmentRepository
import com.isel.leic.ps.ion_classcode.repository.TeamRepository
import org.jdbi.v3.core.Handle

class JdbiTeamRepository(private val handle: Handle): TeamRepository {
    override fun createTeam(team: TeamInput):Int {
        return handle.createUpdate(
            """
            INSERT INTO team (assignment, name,is_created)
            VALUES (:assigmentId, :name,false)
            RETURNING id
            """,
        )
            .bind("assigmentId", team.assigmentId)
            .bind("name", team.name)
            .execute()
    }

    override fun updateTeamStatus(id: Int, status: Boolean) {
        handle.createUpdate(
            """
            UPDATE team SET is_created = :status
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .bind("status", status)
            .execute()
    }

    override fun getTeamById(id: Int): Team? {
        return handle.createQuery(
            """
            SELECT * FROM team
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo(Team::class.java)
            .firstOrNull()
    }

    override fun leaveTeam(teamId: Int, studentId: Int) {
        handle.createUpdate(
            """
            DELETE FROM student_team
            WHERE team = :teamId AND student = :studentId
            """,
        )
            .bind("teamId", teamId)
            .bind("studentId", studentId)
            .execute()
    }

    override fun deleteTeam(teamId: Int) {
        handle.createUpdate(
            """
            DELETE FROM team
            WHERE id = :teamId
            """,
        )
            .bind("teamId", teamId)
            .execute()
    }

    override fun getTeamsFromAssigment(assigmentId: Int): List<Team> {
        return handle.createQuery(
            """
            SELECT * FROM team
            WHERE assignment = :assigmentId
            """,
        )
            .bind("assigmentId", assigmentId)
            .mapTo(Team::class.java)
            .list()
    }

    override fun getTeamsFromStudent(studentId: Int): List<Team> {
        return handle.createQuery(
            """
            SELECT * FROM team
            JOIN student_team on team.id = student_team.team
            WHERE student_team.student = :studentId
            """,
        )
            .bind("studentId", studentId)
            .mapTo(Team::class.java)
            .list()
    }

}