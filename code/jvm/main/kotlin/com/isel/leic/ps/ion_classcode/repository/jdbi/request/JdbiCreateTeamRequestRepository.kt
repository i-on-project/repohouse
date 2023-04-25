package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.CreateTeamInput
import com.isel.leic.ps.ion_classcode.domain.requests.CreateTeam
import com.isel.leic.ps.ion_classcode.repository.request.CreateTeamRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Create Team Request methods
 */
class JdbiCreateTeamRequestRepository(
    private val handle: Handle,
) : CreateTeamRepository {

    /**
     * Method to create a Create Team Request
     */
    override fun createCreateTeamRequest(request: CreateTeamInput,creator:Int): Int {
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

        return handle.createUpdate(
            """
            INSERT INTO createteam (id)
            VALUES (:id)
            """,
        )
            .bind("id", id)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
    }

    /**
     * Method to get all Create Team Request's
     */
    override fun getCreateTeamRequests(): List<CreateTeam> {
        return handle.createQuery(
            """
            SELECT c.id, r.creator, r.state, r.composite FROM createteam as c JOIN request r on r.id = c.id
            """,
        )
            .mapTo<CreateTeam>()
            .list()
    }

    /**
     * Method to get a Create Team Request by is id
     */
    override fun getCreateTeamRequestById(id: Int): CreateTeam? {
        return handle.createQuery(
            """
            SELECT c.id, r.creator, r.state, r.composite FROM createteam as c JOIN request r on r.id = c.id
            WHERE c.id = :id
            """,
        )
            .bind("id", id)
            .mapTo<CreateTeam>()
            .firstOrNull()
    }

    /**
     * Method to get all Create Team Request's by a user
     */
    override fun getCreateTeamRequestsByUser(userId: Int): List<CreateTeam> {
        return handle.createQuery(
            """
            SELECT c.id, r.creator, r.state, r.composite FROM createteam as c JOIN request r on r.id = c.id
            WHERE r.creator = :userId
            """,
        )
            .bind("userId", userId)
            .mapTo<CreateTeam>()
            .list()
    }
}
