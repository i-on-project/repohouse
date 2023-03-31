package com.isel.leic.ps.ion_classcode.repository.jdbi


import com.isel.leic.ps.ion_classcode.repository.CooldownRepository
import java.sql.Timestamp
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiCooldownRepository(private val handle: Handle) : CooldownRepository {

    override fun createCooldownRequest(userId:Int,time: Timestamp): Int? {
        return handle.createUpdate(
            """
            INSERT INTO Cooldown (user_id, end_date)
            VALUES (:user_id,:end_date)
            RETURNING id
            """,
        )
            .bind("user_id", userId)
            .bind("end_date", time)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .firstOrNull()
    }

    override fun getCooldownRequest(userId: Int): Int? {
        val endDate = handle.createQuery(
            """
            SELECT end_date FROM Cooldown
            WHERE user_id = :user_id and end_date > CURRENT_TIMESTAMP
            """,
        )
            .bind("user_id", userId)
            .mapTo<Timestamp>()
            .firstOrNull() ?: return null

        val currentTimeInSeconds = System.currentTimeMillis()/1000
        val endDateInSeconds = endDate.time/1000
        return (endDateInSeconds - currentTimeInSeconds).toInt()
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
