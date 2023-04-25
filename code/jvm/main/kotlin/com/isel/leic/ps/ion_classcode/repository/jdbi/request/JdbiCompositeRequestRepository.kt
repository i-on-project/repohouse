package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.CompositeInput
import com.isel.leic.ps.ion_classcode.domain.requests.Composite
import com.isel.leic.ps.ion_classcode.domain.requests.Request
import com.isel.leic.ps.ion_classcode.repository.request.CompositeRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Composite Request methods
 */
class JdbiCompositeRequestRepository(
    private val handle: Handle,
) : CompositeRepository {

    /**
     * Method to create a Composite Request
     */
    override fun createCompositeRequest(request: CompositeInput,creator:Int): Int {
        val requestId = handle.createUpdate(
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

        val compositeId = handle.createUpdate(
            """
        INSERT INTO composite (id)
        VALUES (:id)
        RETURNING id
        """,
        )
            .bind("id", requestId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()

        request.requests.forEach { id ->
            handle.createUpdate(
                """
                    UPDATE request
                    SET composite = :compositeId
                    WHERE id = :id
                    """,
            )
                .bind("id", id)
                .bind("compositeId", compositeId)
                .execute()
        }
        return compositeId
    }

    /**
     * Method to change a Composite Request state
     */
    override fun changeStateCompositeRequest(id: Int, state: String) {
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
     * Method to get all a Composite Request's
     */
    override fun getCompositeRequests(): List<Composite> {
        val compositeIds = handle.createQuery(
            """
           SELECT id FROM composite
           """,
        )
            .mapTo<Int>()
            .list()
        return compositeIds.map { id ->
            val compositeRequest = handle.createQuery(
                """SELECT * FROM request WHERE id = :id""",
            )
                .bind("id", id)
                .mapTo<Request>()
                .first()
            val requests = handle.createQuery(
                """SELECT * FROM request WHERE composite = :id""",
            )
                .bind("id", id)
                .mapTo<Request>()
                .list()

            Composite(id = id, creator = compositeRequest.creator, state = compositeRequest.state, requests = requests.map { it.id })
        }
    }

    /**
     * Method to get a Composite Request by is id
     */
    override fun getCompositeRequestById(id: Int): Composite? {
        val compositeRequest = handle.createQuery(
            """
          SELECT * FROM request
          WHERE id = :id
          """,
        )
            .bind("id", id)
            .mapTo<Request>()
            .firstOrNull() ?: return null

        val requests = handle.createQuery(
            """
          SELECT * FROM request
          WHERE composite = :id
          """,
        )
            .bind("id", id)
            .mapTo<Request>()
            .list()

        return Composite(id = id, creator = compositeRequest.creator, state = compositeRequest.state, requests = requests.map { it.id })
    }

    /**
     * Method to get all Composite Request's by a user
     */
    override fun getCompositeRequestsByUser(userId: Int): List<Composite> {
        val compositeRequests = handle.createQuery(
            """
            SELECT c.id, creator, composite, state FROM composite AS c JOIN request as r ON c.id = r.id
            WHERE creator = :userId
            """,
        )
            .bind("userId", userId)
            .mapTo<Request>()
            .list()
        return compositeRequests.map { compositeRequest ->
            val ids = handle.createQuery(
                """
                SELECT id FROM request
                WHERE composite = :compositeId
                """,
            )
                .bind("compositeId", compositeRequest.id)
                .mapTo<Int>()
                .list()
            Composite(id = compositeRequest.id, creator = compositeRequest.creator, state = compositeRequest.state, requests = ids)
        }
    }
}
