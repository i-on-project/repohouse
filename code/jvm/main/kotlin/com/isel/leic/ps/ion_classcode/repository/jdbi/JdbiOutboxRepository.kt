package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Outbox
import com.isel.leic.ps.ion_classcode.domain.input.OutboxInput
import com.isel.leic.ps.ion_classcode.repository.OutboxRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.sql.Timestamp

const val INTERVAL = "10 MINUTES"
class JdbiOutboxRepository(private val handle: Handle) : OutboxRepository {
    override fun createOutboxRequest(outbox: OutboxInput): Int? {
        return handle.createUpdate(
            """
            INSERT INTO Outbox (user_id, otp,status,expired_at)
            VALUES (:user_id,:otp,:status,:interval::timestamp)
            RETURNING id
            """,
        )
            .bind("user_id", outbox.userId)
            .bind("otp", outbox.otp)
            .bind("status", "Pending")
            .bind("interval", toTimestamp(INTERVAL))
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

    override fun getOutboxRequest(outboxId: Int): Outbox? {
        return handle.createQuery(
            """
            SELECT * FROM Outbox
            WHERE id = :id
            """,
        )
            .bind("id", outboxId)
            .mapTo<Outbox>()
            .firstOrNull()
    }

    override fun updateOutboxStateRequest(outboxId: Int): Boolean {
        return handle.createUpdate(
            """
            UPDATE Outbox
            SET status = 'Sent'
            WHERE id = :id
            """,
        )
            .bind("id", outboxId)
            .execute() == 1
    }

    override fun getOutboxRequestByUserId(userId: Int): Outbox? {
        return handle.createQuery(
            """
            SELECT * FROM Outbox
            WHERE user_id = :user_id
            """,
        )
            .bind("user_id", userId)
            .mapTo<Outbox>()
            .firstOrNull()
    }

    override fun deleteOutboxRequest(outboxId: Int): Boolean {
        return handle.createUpdate(
            """
            DELETE FROM Outbox
            WHERE id = :id
            """,
        )
            .bind("id", outboxId)
            .execute() == 1
    }

    private fun toTimestamp(interval: String): Timestamp {
        return handle.createQuery(
            """
            SELECT NOW() + :interval::interval AS timestamp
            """,
        )
            .bind("interval", interval)
            .mapTo<Timestamp>()
            .first()
    }
}
