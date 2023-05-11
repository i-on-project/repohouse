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
        return JoinTeam(id, creator, teamId = request.teamId)
    }

    /**
     * Method to get all Join Team Request's
     */
    override fun getJoinTeamRequests(): List<JoinTeam> {
        return handle.createQuery(
            """
            SELECT j.id, r.creator, r.state, j.team_id, r.composite FROM jointeam as j JOIN request r on r.id = j.id
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
            SELECT j.id, r.creator, r.state, j.team_id, r.composite FROM jointeam as j JOIN request r on r.id = j.id
            WHERE j.id = :id
            """,
        )
            .bind("id", id)
            .mapTo<JoinTeam>()
            .firstOrNull()
    }

    /**
     * Method to get all Create Team Request's by a user
     */
    override fun getJoinTeamRequestsByUser(userId: Int): List<JoinTeam> {
        return handle.createQuery(
            """
            SELECT j.id, r.creator, r.state, j.team_id, r.composite FROM jointeam as j JOIN request r on r.id = j.id
            where r.creator = :userId
            """,
        )
            .bind("userId", userId)
            .mapTo<JoinTeam>()
            .list()
    }
}
