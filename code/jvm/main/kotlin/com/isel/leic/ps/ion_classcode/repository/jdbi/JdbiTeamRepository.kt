package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Team
import com.isel.leic.ps.ion_classcode.domain.input.TeamInput
import com.isel.leic.ps.ion_classcode.repository.TeamRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiTeamRepository(private val handle: Handle) : TeamRepository {
    override fun createTeam(team: TeamInput): Int {
        return handle.createUpdate(
            """
            INSERT INTO team (assignment, name, is_created)
            VALUES (:assignmentId, :name,false)
            RETURNING id
            """,
        )
            .bind("assignmentId", team.assignmentId)
            .bind("name", team.name)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
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
            .mapTo<Team>()
            .firstOrNull()
    }

    override fun getStudentsFromTeam(teamId: Int): List<Student> {
        return handle.createQuery(
            """
            SELECT * FROM student
            JOIN student_team on student.id = student_team.student
            WHERE student_team.team = :teamId
            """,
        )
            .bind("teamId", teamId)
            .mapTo<Student>()
            .list()
    }

    override fun enterTeam(teamId: Int, studentId: Int) {
        handle.createUpdate(
            """
            INSERT INTO student_team (student, team)
            VALUES (:student_id, :team_id)
            """,
        )
            .bind("student_id", studentId)
            .bind("team_id", teamId)
            .execute()
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

    override fun getTeamsFromAssignment(assignmentId: Int): List<Team> {
        return handle.createQuery(
            """
            SELECT * FROM team
            WHERE assignment = :assignmentId
            """,
        )
            .bind("assignmentId", assignmentId)
            .mapTo<Team>()
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
            .mapTo<Team>()
            .list()
    }

}
