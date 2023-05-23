package com.isel.leic.ps.ionClassCode.repository.jdbi.request

import com.isel.leic.ps.ionClassCode.domain.input.request.CompositeInput
import com.isel.leic.ps.ionClassCode.domain.requests.Composite
import com.isel.leic.ps.ionClassCode.repository.request.CompositeRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * Implementation of the Composite Request methods
 */
class JdbiCompositeRequestRepository(
    private val handle: Handle,
) : CompositeRepository {

    /**
     * Method to create a Composite Request
     */
    override fun createCompositeRequest(request: CompositeInput, creator: Int): Composite {
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
        return Composite(compositeId, creator)
    }
    companion object{
        private val logger = LoggerFactory.getLogger(JdbiCompositeRequestRepository::class.java)
    }

    /**
     * Method to change a Composite Request state
     */
    override fun updateCompositeState(compositeId: Int) {
        val query = handle.createQuery(
            """
           SELECT r.state FROM request as r JOIN composite c on r.composite = :compositeId
        """,
        )
            .bind("compositeId", compositeId)
            .mapTo<String>()
            .list()
        val state = if (query.all { it == "Accepted" }) "Accepted" else { if (query.any { it == "Not_Concluded" }) "Not_Concluded" else "Rejected" }
        logger.info("Composite request with id $compositeId has state $state")
        handle.createUpdate(
            """
            UPDATE request
            SET state = :state
            WHERE id = :id
            """,
        )
            .bind("id", compositeId)
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
            handle.createQuery(
                """SELECT * FROM request WHERE id = :id""",
            )
                .bind("id", id)
                .mapTo<Composite>()
                .first()
        }
    }

    override fun getCompositeRequestsThatAreNotAccepted(): List<Composite> {
        return handle.createQuery(
            """
           SELECT r.id, r.creator, r.state, r.composite FROM composite JOIN request r on composite.id = r.id
           WHERE state != 'Accepted'
           """,
        )
            .mapTo<Composite>()
            .list()
    }

    /**
     * Method to get a Composite Request by is id
     */
    override fun getCompositeRequestsById(compositeId: Int): List<Int>? {
        return handle.createQuery(
            """
          SELECT id FROM request
          WHERE composite = :id
          """,
        )
            .bind("id", compositeId)
            .mapTo<Int>()
            .list()
    }

    override fun getCompositeRequestById(compositeId: Int): Composite? {
        return handle.createQuery(
            """
          SELECT * FROM request
          WHERE composite = :id
          """,
        )
            .bind("id", compositeId)
            .mapTo<Composite>()
            .firstOrNull()
    }

    /**
     * Method to get all Composite Request's by a user
     */
    override fun getCompositeRequestsByUser(userId: Int): List<Composite> {
        return handle.createQuery(
            """
            SELECT c.id, creator, composite, state FROM composite AS c JOIN request as r ON c.id = r.id
            WHERE creator = :userId
            """,
        )
            .bind("userId", userId)
            .mapTo<Composite>()
            .list()
    }
}
