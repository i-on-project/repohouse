package com.isel.leic.ps.ionClassCode.repository.jdbi.request

import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveTeamInput
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveTeam
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveTeamWithRepoName
import com.isel.leic.ps.ionClassCode.repository.request.LeaveTeamRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Data class to help in function [JdbiLeaveTeamRequestRepository.createLeaveTeamRequest]
 */
private data class HelperLeaveTeam(
    val count: Int,
    val name: String,
)

/**
 * Implementation of the Leave Team Request methods
 */
class JdbiLeaveTeamRequestRepository(
    private val handle: Handle,
) : LeaveTeamRepository {

    /**
     * Method to create a Leave Team Request
     */
    override fun createLeaveTeamRequest(request: LeaveTeamInput, creator: Int): LeaveTeam {
        val id = handle.createUpdate(
            """
                INSERT INTO request (creator, composite, state)
                VALUES (:creator, :compositeId, 'Pending')
                RETURNING id
                """,
        )
            .bind("creator", creator)
            .bind("compositeId", request.composite)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
        handle.createUpdate(
            """
                INSERT INTO leaveteam (id, team_id)
                VALUES (:id, :teamId)
                """,
        )
            .bind("id", id)
            .bind("teamId", request.teamId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
        val githubUsername = handle.createQuery(
            """
                SELECT github_username FROM users
                WHERE id = :creator
                """,
        )
            .bind("creator", creator)
            .mapTo<String>()
            .first()
        val helper = handle.createQuery(
            """
                SELECT COUNT(*), (SELECT t.name FROM team t WHERE id = :teamId) FROM student_team st
                WHERE st.team = :teamId
                """,
        )
            .bind("teamId", request.teamId)
            .mapTo<HelperLeaveTeam>()
            .first()
        return LeaveTeam(id = id, creator = creator, composite = null, teamId = request.teamId, githubUsername = githubUsername, teamName = helper.name, membersCount = helper.count)
    }

    /**
     * Method to get all Leave Team Request's
     */
    override fun getLeaveTeamRequests(): List<LeaveTeam> {
        return handle.createQuery(
            """
                SELECT l.id, x.creator, x.state, x.composite, l.team_id, x.github_username, (SELECT COUNT(*) FROM student_team
                WHERE team = l.team_id) as members_count, (SELECT t.name FROM team t where t.id=l.team_id) as team_name FROM
                (SELECT u.github_username, r.id, r.creator, r.composite, r.state FROM request r JOIN users u on r.creator = u.id) as x JOIN
                 leaveteam as l on x.id = l.id
                """,
        )
            .mapTo<LeaveTeam>()
            .list()
    }

    override fun getLeaveTeamWithRepoNameRequests(teamId: Int): List<LeaveTeamWithRepoName> {
        val requests = handle.createQuery(
            """
                SELECT l.id, x.creator, x.state, x.composite, l.team_id, x.github_username, (SELECT COUNT(*) FROM student_team
                WHERE team = l.team_id) as members_count, (SELECT t.name FROM team t where t.id=l.team_id) as team_name FROM
                (SELECT u.github_username, r.id, r.creator, r.composite, r.state FROM request r JOIN users u on r.creator = u.id) as x JOIN
                 leaveteam as l on x.id = l.id
                 where l.team_id = :teamId
                """,
        )
            .bind("teamId", teamId)
            .mapTo<LeaveTeam>()
            .list()
        return requests.map { leaveTeam ->
            val repoName = handle.createQuery(
                """
                    SELECT r.name FROM repo r JOIN team t on r.team_id = t.id
                    WHERE t.id = :teamId
                    """,
            )
                .bind("teamId", leaveTeam.teamId)
                .mapTo<String>()
                .first()
            LeaveTeamWithRepoName(repoName = repoName, leaveTeam = leaveTeam)
        }
    }

    override fun getLeaveTeamWithRepoNameRequestsFromClassroom(classroomId: Int, compositeId: Int): List<LeaveTeamWithRepoName> {
        val ids = handle.createQuery(
            """
            SELECT t.id FROM team t join (select a.id as assId from assignment a join classroom c on c.id = a.classroom_id where c.id = :classroomId) as x
            on t.assignment = x.assId
            """,
        )
            .bind("classroomId", classroomId)
            .mapTo<Int>()
            .list()
        val requests = ids.mapNotNull {
            handle.createQuery(
            """
                SELECT l.id, x.creator, x.state, x.composite, l.team_id, x.github_username, (SELECT COUNT(*) FROM student_team
                WHERE team = l.team_id) as members_count, (SELECT t.name FROM team t where t.id=l.team_id) as team_name FROM
                (SELECT u.github_username, r.id, r.creator, r.composite, r.state FROM request r JOIN users u on r.creator = u.id) as x JOIN
                 leaveteam as l on x.id = l.id
                 where l.team_id = :teamId and x.composite = :compositeId
            """
            )
                .bind("teamId", it)
                .bind("compositeId", compositeId)
                .mapTo<LeaveTeam>()
                .firstOrNull()
        }
        return requests.map { leaveTeam ->
            val repoName = handle.createQuery(
                """
                    SELECT r.name FROM repo r JOIN team t on r.team_id = t.id
                    WHERE t.id = :teamId
                    """,
            )
                .bind("teamId", leaveTeam.teamId)
                .mapTo<String>()
                .first()
            LeaveTeamWithRepoName(repoName = repoName, leaveTeam = leaveTeam)
        }
    }
    /**
     * Method to get a Leave Team Request by is id
     */
    override fun getLeaveTeamRequestById(id: Int): LeaveTeam? {
        return handle.createQuery(
            """
                SELECT l.id, x.creator, x.state, x.composite, l.team_id, x.github_username, (SELECT COUNT(*) FROM student_team
                WHERE team = l.team_id) as members_count, (SELECT t.name FROM team t where t.id=l.team_id) as team_name FROM 
                (SELECT u.github_username, r.id, r.creator, r.composite, r.state FROM request r JOIN users u on r.creator = u.id WHERE r.id = :id) as x JOIN
                leaveteam l on x.id = l.id
                """,
        )
            .bind("id", id)
            .mapTo<LeaveTeam>()
            .firstOrNull()
    }

    override fun getLeaveTeamRequestsByCompositeId(compositeId: Int): List<LeaveTeam> {
        return handle.createQuery(
            """
                SELECT l.id, x.creator, x.state, x.composite, l.team_id, x.github_username, (SELECT COUNT(*) FROM student_team
                WHERE team = l.team_id) as members_count, (SELECT t.name FROM team t where t.id=l.team_id) as team_name FROM 
                (SELECT u.github_username, r.id, r.creator, r.composite, r.state FROM request r JOIN users u on r.creator = u.id WHERE r.composite = :compositeId AND r.state!='Accepted') as x JOIN
                leaveteam l on x.id = l.id
                """,
        )
            .bind("compositeId", compositeId)
            .mapTo<LeaveTeam>()
            .list()
    }

    override fun updateLeaveTeamState(requestId: Int, state: String) {
        handle.createUpdate(
            """
                UPDATE request
                SET state = :state
                WHERE id = :requestId
                """,
        )
            .bind("requestId", requestId)
            .bind("state", state)
            .execute()
    }
}
