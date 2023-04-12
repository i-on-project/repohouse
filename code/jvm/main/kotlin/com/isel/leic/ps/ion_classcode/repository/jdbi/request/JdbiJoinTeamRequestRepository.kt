package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.JoinTeamInput
import com.isel.leic.ps.ion_classcode.domain.requests.JoinTeam
import com.isel.leic.ps.ion_classcode.repository.request.JoinTeamRepository
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
    override fun createJoinTeamRequest(request: JoinTeamInput): Int {
        val id = handle.createUpdate(
            """
            INSERT INTO request (creator, composite,state)
            VALUES (:creator, :compositeId, 'Pending')
            RETURNING id
            """,
        )
            .bind("creator", request.creator)
            .bind("compositeId", request.composite)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()

        return handle.createUpdate(
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
