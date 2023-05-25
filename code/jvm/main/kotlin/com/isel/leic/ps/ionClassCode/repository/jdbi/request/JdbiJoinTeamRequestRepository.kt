package com.isel.leic.ps.ionClassCode.repository.jdbi.request

import com.isel.leic.ps.ionClassCode.domain.input.request.JoinTeamInput
import com.isel.leic.ps.ionClassCode.domain.requests.JoinTeam
import com.isel.leic.ps.ionClassCode.repository.request.JoinTeamRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Join Team Request methods
 */
class JdbiJoinTeamRequestRepository(
    private val handle: Handle,
) : JoinTeamRepository {

    /**
     * Method to create a Join Team Request
     */
    override fun createJoinTeamRequest(request: JoinTeamInput, creator: Int): JoinTeam {
        val id = handle.createUpdate(
            """
            INSERT INTO request (creator, composite,state)
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
            INSERT INTO jointeam (id, team_id, assigment_id)
            VALUES (:id, :teamId, :assigmentId)
            """,
        )
            .bind("id", id)
            .bind("teamId", request.teamId)
            .bind("assigmentId", request.assignmentId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
        return JoinTeam(id, creator, teamId = request.teamId, githubUsername = request.creatorGitHubUserName)
    }

    /**
     * Method to get all Join Team Request's
     */
    override fun getJoinTeamRequests(): List<JoinTeam> {
        return handle.createQuery(
            """
            SELECT j.id, x.creator, x.state, j.team_id, x.composite, x.github_username FROM jointeam as j JOIN 
            (SELECT r.id, r.creator, r.state, r.composite, u.github_username FROM request r JOIN users u on r.creator = u.id) AS x
            on j.id = x.id
            """,
        )
            .mapTo<JoinTeam>()
            .list()
    }

    /**
     * Method to get a Join Team Request by is id
     */
    override fun getJoinTeamRequestById(id: Int): JoinTeam? {
        return handle.createQuery(
            """
            SELECT j.id, x.creator, x.state, j.team_id, x.composite, x.github_username FROM jointeam as j JOIN 
            (SELECT r.id, r.creator, r.state, r.composite, u.github_username FROM request r JOIN users u on r.creator = u.id) AS x on x.id = j.id
            WHERE j.id = :id
            """,
        )
            .bind("id", id)
            .mapTo<JoinTeam>()
            .firstOrNull()
    }

    /**
     * Method to get a Join Team Request by is composite id
     */
    override fun getJoinTeamRequestByCompositeId(compositeId: Int): JoinTeam? {
        return handle.createQuery(
            """
            SELECT x.id, x.creator, x.state, x.composite, j.team_id, x.github_username FROM (
                SELECT u.github_username, r2.id, r2.creator, r2.state, r2.composite FROM users as u JOIN request r2 on u.id = r2.creator
                WHERE r2.composite = :compositeId
            ) as x JOIN jointeam j on j.id = x.id
            """,
        )
            .bind("compositeId", compositeId)
            .mapTo<JoinTeam>()
            .firstOrNull()
    }

    /**
     * Method to update a Join Team Request state
     */
    override fun updateJoinTeamState(requestId: Int, state: String) {
        handle.createUpdate(
            """
            UPDATE request
            SET state = :state
            WHERE id = :id
            """,
        )
            .bind("id", requestId)
            .bind("state", state)
            .execute()
    }
}
