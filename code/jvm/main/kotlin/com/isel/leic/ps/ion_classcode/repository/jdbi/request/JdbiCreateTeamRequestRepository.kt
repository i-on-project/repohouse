package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.*
import com.isel.leic.ps.ion_classcode.domain.requests.*
import com.isel.leic.ps.ion_classcode.repository.request.*
import org.jdbi.v3.core.Handle

class JdbiCreateTeamRequestRepository(
    private val handle: Handle,
) : CreateTeamRepository {

    override fun createCreateTeamRequest(request: CreateTeamInput): Int {
        val id = handle.createUpdate(
            """
            INSERT INTO request (creator, composite,state)
            VALUES (:creator, :compositeId,'pending')
            RETURNING id
            """
        )
            .bind("creator", request.creator)
            .bind("composite", request.composite)
            .execute()

        return handle.createUpdate(
            """
            INSERT INTO createteam (id, team_id)
            VALUES (:id, :teamId)
            """
        )
            .bind("id", id)
            .bind("teamId", request.teamId)
            .execute()
    }

    override fun getCreateTeamRequests(): List<CreateTeam> {
        return handle.createQuery(
            """
            SELECT * FROM createteam
            """
        )
            .mapTo(CreateTeam::class.java)
            .list()
    }

    override fun getCreateTeamRequestById(id: Int): CreateTeam {
        return handle.createQuery(
            """
            SELECT * FROM createteam
            WHERE id = :id
            """
        )
            .bind("id", id)
            .mapTo(CreateTeam::class.java)
            .first()
    }

    override fun getCreateTeamRequestsByUser(userId: Int): List<CreateTeam> {
        return handle.createQuery(
            """
            SELECT * FROM createteam
            WHERE id IN (
                SELECT id FROM request
                WHERE creator = :userId
            )
            """
        )
            .bind("userId", userId)
            .mapTo(CreateTeam::class.java)
            .list()
    }
}
