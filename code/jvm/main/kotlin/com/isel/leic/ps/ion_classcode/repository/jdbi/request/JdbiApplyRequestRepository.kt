package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.ApplyInput
import com.isel.leic.ps.ion_classcode.domain.requests.Apply
import com.isel.leic.ps.ion_classcode.repository.request.ApplyRequestRepository
import org.jdbi.v3.core.Handle

class JdbiApplyRequestRepository(
    private val handle: Handle,
) : ApplyRequestRepository {

    override fun createApplyRequest(request: ApplyInput): Int {
        val requestId = handle.createUpdate(
            """
            INSERT INTO request (creator, composite,state)
            VALUES (:creator, :compositeId,'pending')
            RETURNING id
            """,
        )
            .bind("creator", request.creator)
            .bind("compositeId", request.composite)
            .execute()

        return handle.createUpdate(
            """
            INSERT INTO apply (id,teacher_id)
            VALUES (:creator, :teacher_id)
            RETURNING id
            """,
        )
            .bind("id", requestId)
            .bind("teacher_id", request.teacherId)
            .execute()
    }

    override fun getApplyRequests(): List<Apply> {
        return handle.createQuery(
            """
            SELECT * FROM apply
            """,
        )
            .mapTo(Apply::class.java)
            .list()
    }

    override fun getApplyRequestById(id: Int): Apply {
        return handle.createQuery(
            """
            SELECT * FROM apply
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo(Apply::class.java)
            .first()
    }

    override fun getApplyRequestsByUser(teacherId: Int): List<Apply> {
        return handle.createQuery(
            """
            SELECT * FROM apply
            WHERE teacher_id = :teacherId
            """,
        )
            .bind("teacherId", teacherId)
            .mapTo(Apply::class.java)
            .list()
    }
}
