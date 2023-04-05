package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.ApplyInputInterface
import com.isel.leic.ps.ion_classcode.domain.requests.Apply
import com.isel.leic.ps.ion_classcode.repository.request.ApplyRequestRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiApplyRequestRepository(
    private val handle: Handle,
) : ApplyRequestRepository {

    override fun createApplyRequest(request: ApplyInputInterface): Int {
        val requestId = handle.createUpdate(
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
            INSERT INTO apply (id, teacher_id)
            VALUES (:id, :teacher_id)
            RETURNING id
            """,
        )
            .bind("id", requestId)
            .bind("teacher_id", request.creator)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
    }

    override fun getApplyRequests(): List<Apply> {
        return handle.createQuery(
            """
            SELECT r.id, r.creator, r.state, r.composite FROM apply JOIN request r on r.id = apply.id
            """,
        )
            .mapTo(Apply::class.java)
            .list()
    }

    override fun getApplyRequestById(id: Int): Apply? {
        return handle.createQuery(
            """
            SELECT r.id, r.creator, r.state, r.composite FROM apply JOIN request r on r.id = apply.id
            WHERE apply.id = :id
            """,
        )
            .bind("id", id)
            .mapTo<Apply>()
            .firstOrNull()
    }

    override fun getApplyRequestsByUser(teacherId: Int): List<Apply> {
        return handle.createQuery(
            """
            SELECT r.id, r.creator, r.state, r.composite FROM apply JOIN request r on r.id = apply.id
            WHERE apply.teacher_id = :teacherId
            """,
        )
            .bind("teacherId", teacherId)
            .mapTo<Apply>()
            .list()
    }
}
