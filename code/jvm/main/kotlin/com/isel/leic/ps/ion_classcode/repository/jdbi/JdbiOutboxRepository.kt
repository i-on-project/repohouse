package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Outbox
import com.isel.leic.ps.ion_classcode.domain.input.OutboxInput
import com.isel.leic.ps.ion_classcode.repository.OutboxRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.sql.Timestamp
import java.time.Instant


/**
 * Implementation of the Outbox methods
 */
class JdbiOutboxRepository(private val handle: Handle) : OutboxRepository {

    /**
     * Method to create an Outbox
     */
    override fun createOutboxRequest(outbox: OutboxInput): Outbox {
        handle.createUpdate(
            """
            INSERT INTO Outbox (user_id, status, sent_at)
            VALUES (:user_id,:status,CURRENT_TIMESTAMP)
            RETURNING user_id
            """,
        )
            .bind("user_id", outbox.userId)
            .bind("status", "Pending")
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
        return Outbox(outbox.userId, "Pending", Timestamp(System.currentTimeMillis()))
    }

    /**
     * Method to get all Outbox's pending sent
     */
    override fun getOutboxPendingRequests(): List<Outbox> {
        return handle.createQuery(
            """
            SELECT * FROM outbox
            WHERE status = 'Pending'
            """,
        )
            .mapTo<Outbox>()
            .list()
    }

    /**
     * Method to get all Outbox's
     */
    override fun getOutboxRequest(userId: Int): Outbox? {
        return handle.createQuery(
            """
            SELECT * FROM Outbox
            WHERE user_id = :id
            """,
        )
            .bind("id", userId)
            .mapTo<Outbox>()
            .firstOrNull()
    }

    /**
     * Method to update a Outbox state
     */
    override fun updateOutboxStateRequest(userId: Int, state:String) {
        handle.createUpdate(
            """
            UPDATE Outbox
            SET status = :state
            WHERE user_id = :id
            """,
        )
            .bind("state",state)
            .bind("id", userId)
            .execute()
    }

    /**
     * Method to update an Outbox send time
     */
    override fun updateOutboxSentTimeRequest(userId: Int) {
        handle.createUpdate(
            """
            UPDATE Outbox
            SET sent_at = :sent_at
            WHERE user_id = :id
            """,
        )
            .bind("sent_at",Timestamp.from(Instant.now()))
            .bind("id", userId)
            .execute()
    }

    /**
     * Method to delete an Outbox
     */
    override fun deleteOutboxRequest(userId: Int) {
        handle.createUpdate(
            """
            DELETE FROM Outbox
            WHERE user_id = :id
            """,
        )
            .bind("id", userId)
            .execute()
    }
}
