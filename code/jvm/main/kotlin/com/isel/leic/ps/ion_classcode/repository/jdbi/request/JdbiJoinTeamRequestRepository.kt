package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.JoinTeamInput
import com.isel.leic.ps.ion_classcode.domain.requests.JoinTeam
import com.isel.leic.ps.ion_classcode.repository.request.JoinTeamRepository
import org.jdbi.v3.core.Handle

class JdbiJoinTeamRequestRepository(
    private val handle: Handle,
) : JoinTeamRepository {

    override fun createJoinTeamRequest(request: JoinTeamInput): Int {
        val id = handle.createUpdate(
            """
            INSERT INTO request (creator, composite,state)
            VALUES (:creator, :compositeId,'pending')
            RETURNING id
            """,
        )
            .bind("creator", request.creator)
            .bind("composite", request.composite)
            .execute()

        return handle.createUpdate(
            """
            INSERT INTO jointeam (id, team_id)
            VALUES (:id, :teamId)
            """,
        )
            .bind("id", id)
            .bind("teamId", request.teamId)
            .execute()
    }

    override fun getJoinTeamRequests(): List<JoinTeam> {
        return handle.createQuery(
            """
            SELECT * FROM jointeam
            """,
        )
            .mapTo(JoinTeam::class.java)
            .list()
    }

    override fun getJoinTeamRequestById(id: Int): JoinTeam {
        return handle.createQuery(
            """
            SELECT * FROM jointeam
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo(JoinTeam::class.java)
            .first()
    }

    override fun getJoinTeamRequestsByUser(userId: Int): List<JoinTeam> {
        return handle.createQuery(
            """
            SELECT * FROM jointeam
            WHERE id IN (
                SELECT id FROM request
                WHERE creator = :userId
            )
            """,
        )
            .bind("userId", userId)
            .mapTo(JoinTeam::class.java)
            .list()
    }
}
