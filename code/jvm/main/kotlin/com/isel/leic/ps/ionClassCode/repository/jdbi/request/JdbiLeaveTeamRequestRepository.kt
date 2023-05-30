package com.isel.leic.ps.ionClassCode.repository.jdbi.request

import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveTeamInput
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveTeam
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
