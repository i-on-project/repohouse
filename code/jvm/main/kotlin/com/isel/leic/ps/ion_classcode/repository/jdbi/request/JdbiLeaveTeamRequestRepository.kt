package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.LeaveTeamInput
import com.isel.leic.ps.ion_classcode.domain.requests.LeaveTeam
import com.isel.leic.ps.ion_classcode.repository.request.LeaveTeamRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Leave Team Request methods
 */
class JdbiLeaveTeamRequestRepository(
    private val handle: Handle,
) : LeaveTeamRepository {

    /**
     * Method to create a Leave Team Request
     */
    override fun createLeaveTeamRequest(request: LeaveTeamInput): Int {
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
                INSERT INTO leaveteam (id, team_id)
                VALUES (:id, :teamId)
                """,
        )
            .bind("id", id)
            .bind("teamId", request.teamId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
    }

    /**
     * Method to get all Leave Team Request's
     */
    override fun getLeaveTeamRequests(): List<LeaveTeam> {
        return handle.createQuery(
            """
                SELECT l.id, r.creator, r.state, l.team_id, r.composite FROM leaveteam as l JOIN request r on r.id = l.id
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
                SELECT l.id, r.creator, r.state, l.team_id, r.composite FROM leaveteam as l JOIN request r on r.id = l.id
                WHERE l.id = :id
                """,
        )
            .bind("id", id)
            .mapTo<LeaveTeam>()
            .firstOrNull()
    }

    /**
     * Method to get all Leave Team Request's by a user
     */
    override fun getLeaveTeamRequestsByUser(userId: Int): List<LeaveTeam> {
        return handle.createQuery(
            """
                SELECT l.id, r.creator, r.state, l.team_id, r.composite FROM leaveteam as l JOIN request r on r.id = l.id
                WHERE r.creator = :creator
                """,
        )
            .bind("creator", userId)
            .mapTo<LeaveTeam>()
            .list()
    }
}
