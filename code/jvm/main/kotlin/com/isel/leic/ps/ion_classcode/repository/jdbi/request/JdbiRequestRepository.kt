package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.RequestInput
import com.isel.leic.ps.ion_classcode.domain.requests.Request
import com.isel.leic.ps.ion_classcode.repository.request.RequestRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Request methods
 */
class JdbiRequestRepository(
    private val handle: Handle,
) : RequestRepository {

    /**
     * Method to create a Request
     */
    override fun createRequest(request: RequestInput,creator:Int): Int {
        return handle.createUpdate(
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
    }

    /**
     * Method to change a Request state
     */
    override fun changeStateRequest(id: Int, state: String) {
        handle.createUpdate(
            """
            UPDATE request
            SET state = :state
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .bind("state", state)
            .execute()
    }

    /**
     * Method to get all Request's
     */
    override fun getRequests(): List<Request> {
        return handle.createQuery(
            """
            SELECT * FROM request
            """,
        )
            .mapTo<Request>()
            .list()
    }

    /**
     * Method to get a Request by is user
     */
    override fun getRequestsByUser(userId: Int): List<Request> {
        return handle.createQuery(
            """
            SELECT * FROM request
            WHERE creator = :userId
            """,
        )
            .bind("userId", userId)
            .mapTo<Request>()
            .list()
    }

    /**
     * Method to verify if Request is a Composite Request
     */
    override fun checkIfIsComposite(id: Int): Boolean {
        return handle.createQuery(
            """
            SELECT * FROM composite
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo<Int>()
            .firstOrNull() != null
    }

    /**
     * Method to get a Request by is id
     */
    override fun getRequestById(id: Int): Request? {
        return handle.createQuery(
            """
            SELECT * FROM request
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo<Request>()
            .firstOrNull()
    }
}
