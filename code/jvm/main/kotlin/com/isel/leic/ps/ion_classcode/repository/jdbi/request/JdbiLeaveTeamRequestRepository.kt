package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.*
import com.isel.leic.ps.ion_classcode.domain.requests.*
import com.isel.leic.ps.ion_classcode.repository.request.*
import org.jdbi.v3.core.Handle

class JdbiLeaveTeamRequestRepository(
    private val handle: Handle,
) : LeaveTeamRepository {

    override fun createLeaveTeamRequest(request: LeaveTeamInput): Int {
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
                INSERT INTO leaveteam (id, team_id)
                VALUES (:id, :teamId)
                """
        )
            .bind("id", id)
            .bind("teamId", request.teamId)
            .execute()
    }

    override fun getLeaveTeamRequests(): List<LeaveTeam> {
        return handle.createQuery(
            """
                SELECT * FROM leaveteam
                """
        )
            .mapTo(LeaveTeam::class.java)
            .list()
    }

    override fun getLeaveTeamRequestById(id: Int): LeaveTeam {
        return handle.createQuery(
            """
                SELECT * FROM leaveteam
                WHERE id = :id
                """
        )
            .bind("id", id)
            .mapTo(LeaveTeam::class.java)
            .first()
    }

    override fun getLeaveTeamRequestsByUser(userId: Int): List<LeaveTeam> {
        return handle.createQuery(
            """
                SELECT * FROM leaveteam
                WHERE id IN (
                    SELECT id FROM request
                    WHERE creator = :creator
                )
                """
        )
            .bind("creator", userId)
            .mapTo(LeaveTeam::class.java)
            .list()
    }
}
