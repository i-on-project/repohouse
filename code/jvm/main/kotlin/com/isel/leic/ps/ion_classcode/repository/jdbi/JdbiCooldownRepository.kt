package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Outbox
import com.isel.leic.ps.ion_classcode.domain.input.OutboxInput
import com.isel.leic.ps.ion_classcode.repository.CooldownRepository
import com.isel.leic.ps.ion_classcode.repository.OutboxRepository
import java.sql.Timestamp
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiCooldownRepository(private val handle: Handle) : CooldownRepository {

    override fun createCooldownRequest(userId:Int,time: Timestamp): Int? {
        return handle.createUpdate(
            """
            INSERT INTO Cooldown (user_id, end_date)
            VALUES (:user_id,:time)
            RETURNING id
            """,
        )
            .bind("user_id", userId)
            .bind("end_date", time)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .firstOrNull()
    }

    override fun getCooldownRequest(userId: Int): Boolean {
        return handle.createQuery(
            """
            SELECT end_date FROM Cooldown
            WHERE user_id = :user_id and end_date > CURRENT_TIMESTAMP
            """,
        )
            .bind("user_id", userId)
            .mapTo<Timestamp>()
            .firstOrNull() != null

    }

    override fun deleteCooldownRequest(userId: Int): Boolean {
        return handle.createUpdate(
            """
            DELETE FROM Cooldown
            WHERE user_id = :user_id
            """,
        )
            .bind("user_id", userId)
            .execute() == 1
    }
}
