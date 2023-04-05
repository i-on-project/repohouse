package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.JoinTeamInputInterface
import com.isel.leic.ps.ion_classcode.domain.requests.JoinTeam
import com.isel.leic.ps.ion_classcode.repository.request.JoinTeamRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiJoinTeamRequestRepository(
    private val handle: Handle,
) : JoinTeamRepository {

    override fun createJoinTeamRequest(request: JoinTeamInputInterface): Int {
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
            INSERT INTO jointeam (id, team_id)
            VALUES (:id, :teamId)
            """,
        )
            .bind("id", id)
            .bind("teamId", request.teamId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
    }

    override fun getJoinTeamRequests(): List<JoinTeam> {
        return handle.createQuery(
            """
            SELECT j.id, r.creator, r.state, j.team_id, r.composite FROM jointeam as j JOIN request r on r.id = j.id
            """,
        )
            .mapTo<JoinTeam>()
            .list()
    }

    override fun getJoinTeamRequestById(id: Int): JoinTeam {
        return handle.createQuery(
            """
            SELECT j.id, r.creator, r.state, j.team_id, r.composite FROM jointeam as j JOIN request r on r.id = j.id
            WHERE j.id = :id
            """,
        )
            .bind("id", id)
            .mapTo<JoinTeam>()
            .first()
    }

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
