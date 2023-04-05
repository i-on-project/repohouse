package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.CreateTeamInputInterface
import com.isel.leic.ps.ion_classcode.domain.requests.CreateTeam
import com.isel.leic.ps.ion_classcode.repository.request.CreateTeamRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiCreateTeamRequestRepository(
    private val handle: Handle,
) : CreateTeamRepository {

    override fun createCreateTeamRequest(request: CreateTeamInputInterface): Int {
        val id = handle.createUpdate(
            """
            INSERT INTO request (creator, composite, state)
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
            INSERT INTO createteam (id)
            VALUES (:id)
            """,
        )
            .bind("id", id)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
    }

    override fun getCreateTeamRequests(): List<CreateTeam> {
        return handle.createQuery(
            """
            SELECT c.id, r.creator, r.state, c.team_id, r.composite FROM createteam as c JOIN request r on r.id = c.id
            """,
        )
            .mapTo<CreateTeam>()
            .list()
    }

    override fun getCreateTeamRequestById(id: Int): CreateTeam? {
        return handle.createQuery(
            """
            SELECT c.id, r.creator, r.state, c.team_id, r.composite FROM createteam as c JOIN request r on r.id = c.id
            WHERE c.id = :id
            """,
        )
            .bind("id", id)
            .mapTo<CreateTeam>()
            .firstOrNull()
    }

    override fun getCreateTeamRequestsByUser(userId: Int): List<CreateTeam> {
        return handle.createQuery(
            """
            SELECT c.id, r.creator, r.state, c.team_id, r.composite FROM createteam as c JOIN request r on r.id = c.id
            WHERE r.creator = :userId
            """,
        )
            .bind("userId", userId)
            .mapTo<CreateTeam>()
            .list()
    }
}
