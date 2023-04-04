package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Outbox
import com.isel.leic.ps.ion_classcode.domain.input.OutboxInput
import com.isel.leic.ps.ion_classcode.repository.OutboxRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.sql.Timestamp
import java.time.Instant

const val INTERVAL = "10 MINUTES"
class JdbiOutboxRepository(private val handle: Handle) : OutboxRepository {
    override fun createOutboxRequest(outbox: OutboxInput): Int? {
        return handle.createUpdate(
            """
            INSERT INTO Outbox (user_id, otp, status, expired_at, sent_at)
            VALUES (:user_id,:otp,:status,:expired_at,:sent_at)
            RETURNING user_id
            """,
        )
            .bind("user_id", outbox.userId)
            .bind("otp", outbox.otp)
            .bind("status", "Pending")
            .bind("expired_at", toTimestamp())
            .bind("sent_at", Timestamp.from(Instant.now()))
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .firstOrNull()
    }

    override fun getOutboxPendingRequests(): List<Outbox> {
        return handle.createQuery(
            """
            SELECT * FROM Outbox
            WHERE status = 'Pending'
            """,
        )
            .mapTo<Outbox>()
            .list()
    }

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

    override fun updateOutboxStateRequest(userId: Int): Boolean {
        return handle.createUpdate(
            """
            UPDATE Outbox
            SET status = 'Sent'
            WHERE user_id = :id
            """,
        )
            .bind("id", userId)
            .execute() == 1
    }

    override fun deleteOutboxRequest(userId: Int): Boolean {
        return handle.createUpdate(
            """
            DELETE FROM Outbox
            WHERE user_id = :id
            """,
        )
            .bind("id", userId)
            .execute() == 1
    }

    private fun toTimestamp(): Timestamp {
        return handle.createQuery(
            """
            SELECT NOW() + :interval::interval AS timestamp
            """,
        )
            .bind("interval", INTERVAL)
            .mapTo<Timestamp>()
            .first()
    }
}
