package com.isel.leic.ps.ionClassCode.repository.jdbi

import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.Team
import com.isel.leic.ps.ionClassCode.domain.input.TeamInput
import com.isel.leic.ps.ionClassCode.repository.TeamRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Team methods
 */
class JdbiTeamRepository(private val handle: Handle) : TeamRepository {

    /**
     * Method to create a Team
     */
    override fun createTeam(team: TeamInput): Team {
        val id = handle.createUpdate(
            """
            INSERT INTO team (assignment, name, is_created)
            VALUES (:assignmentId, :name, false)
            RETURNING id
            """,
        )
            .bind("assignmentId", team.assignmentId)
            .bind("name", team.name)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
        val teamName = "${team.name} - $id"
        handle.createUpdate(
            """
                UPDATE team set name = :name
                WHERE id = :id
                RETURNING name
            """,
        )
            .bind("id", id)
            .bind("name", teamName)
        return Team(id, teamName, false, team.assignmentId)
    }

    /**
     * Method to update a Team status
     */
    override fun updateTeamStatus(id: Int) {
        handle.createUpdate(
            """
            UPDATE team SET is_created = :is_created
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .bind("is_created", true)
            .execute()
    }

    /**
     * Method to get a Team by is id
     */
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

    /**
     * Method to get all students from a Team
     */
    override fun getStudentsFromTeam(teamId: Int): List<Student> {
        return handle.createQuery(
            """
            SELECT users.name,users.email,users.id,users.github_username,users.github_id,users.is_created,users.token, school_id  FROM student
            join users on student.id = users.id
            JOIN student_team on student.id = student_team.student
            WHERE student_team.team = :teamId
            """,
        )
            .bind("teamId", teamId)
            .mapTo<Student>()
            .list()
    }

    override fun addStudentToTeam(teamId: Int, studentId: Int) {
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

    /**
     * Method to enter a Team
     */
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

    /**
     * Method to leave a Team
     */
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

    /**
     * Method to delete a Team
     */
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

    /**
     * Method to get all teams from an assignment
     */
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

    /**
     * Method to get all teams from a student
     */
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
