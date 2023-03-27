package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.*
import com.isel.leic.ps.ion_classcode.domain.requests.Composite
import com.isel.leic.ps.ion_classcode.domain.requests.Request
import com.isel.leic.ps.ion_classcode.repository.request.CompositeRepository
import org.jdbi.v3.core.Handle

class JdbiCompositeRequestRepository(
    private val handle: Handle,
) : CompositeRepository {

    override fun createCompositeRequest(request: CompositeInput): Int {

        val requestId = handle.createUpdate(
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
        INSERT INTO composite (id)
        VALUES (:creator)
        RETURNING id
        """
        )
            .bind("id", requestId)
            .execute()
    }

    override fun changeStatusCompositeRequest(id: Int, status: String) {
        handle.createUpdate(
            """
            UPDATE request
            SET state = :state
            WHERE id = :id
            """
        )
            .bind("id", id)
            .bind("state", status)
            .execute()
    }

    override fun getCompositeRequests(): List<Composite> {
        val compositeIds = handle.createQuery(
            """
           SELECT id FROM composite
           """
        )
            .mapTo(Int::class.java)
            .list()

        return compositeIds.map { getCompositeRequestById(it) }
    }

    override fun getCompositeRequestById(id: Int): Composite {
        val compositeRequest = handle.createQuery(
            """
          SELECT * FROM request
          WHERE id = :id
          """
        )
            .bind("id", id)
            .mapTo(Request::class.java)
            .first()

        val requests = handle.createQuery(
            """
          SELECT * FROM request
          WHERE composite = :id
          """
        )
            .bind("id", id)
            .mapTo(Request::class.java)
            .list()

        return Composite(id, compositeRequest.creator, compositeRequest.state, requests.map { it.id })
    }

    override fun getCompositeRequestsByUser(userId: Int): List<Composite> {
        val compositeList = mutableListOf<Composite>()

        val compositeRequest = handle.createQuery(
            """
            SELECT * FROM request
            WHERE creator = :userId
            """
        )
            .bind("userId", userId)
            .mapTo(Request::class.java)
            .list()

        compositeRequest.forEach {
            val requests = handle.createQuery(
                """
                SELECT * FROM request
                WHERE composite = :id
                """
            )
                .bind("id", it.id)
                .mapTo(Request::class.java)
                .list()
            requests.map {
                compositeList.add(Composite(it.id, it.creator, it.state, requests.map { it.id }))
            }
        }

        return compositeList
    }
}
