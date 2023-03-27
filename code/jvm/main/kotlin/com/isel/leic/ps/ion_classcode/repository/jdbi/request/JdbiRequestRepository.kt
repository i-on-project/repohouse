package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.RequestInput
import com.isel.leic.ps.ion_classcode.domain.requests.Request
import com.isel.leic.ps.ion_classcode.repository.request.RequestRepository
import org.jdbi.v3.core.Handle

class JdbiRequestRepository(
    private val handle: Handle,
) : RequestRepository {
    override fun createRequest(request: RequestInput): Int {
        return handle.createUpdate(
            """
            INSERT INTO request (creator, composite,state)
            VALUES (:creator, :compositeId,'pending')
            RETURNING id
            """,
        )
            .bind("creator", request.creator)
            .bind("composite", request.composite)
            .execute()
    }

    override fun changeStatusRequest(id: Int, status: String) {
        handle.createUpdate(
            """
            UPDATE request
            SET state = :status
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .bind("status", status)
            .execute()
    }

    override fun getRequests(): List<Request> {
        return handle.createQuery(
            """
            SELECT * FROM request
            """,
        )
            .mapTo(Request::class.java)
            .list()
    }

    override fun getRequestsByUser(userId: Int): List<Request> {
        return handle.createQuery(
            """
            SELECT * FROM request
            WHERE creator = :userId
            """,
        )
            .bind("userId", userId)
            .mapTo(Request::class.java)
            .list()
    }

    override fun getRequestById(id: Int): Request {
        return handle.createQuery(
            """
            SELECT * FROM request
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo(Request::class.java)
            .first()
    }
}
