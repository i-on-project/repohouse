package com.isel.leic.ps.ionClassCode.repository.jdbi

import com.isel.leic.ps.ionClassCode.repository.CooldownRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.sql.Timestamp

/**
 * Implementation of the Cooldown methods
 */
class JdbiCooldownRepository(private val handle: Handle) : CooldownRepository {

    /**
     * Method to create a Cooldown
     */
    override fun createCooldownRequest(userId: Int, endTime: Timestamp): Int {
        return handle.createUpdate(
            """
            INSERT INTO Cooldown (user_id, end_date)
            VALUES (:user_id,:end_date)
            RETURNING id
            """,
        )
            .bind("user_id", userId)
            .bind("end_date", endTime)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
    }

    /**
     * Method to get a Cooldown by is id
     */
    override fun getCooldownRequestRemainingTime(userId: Int): Int? {
        val endDate = handle.createQuery(
            """
            SELECT end_date FROM Cooldown
            WHERE user_id = :user_id and end_date > CURRENT_TIMESTAMP
            """,
        )
            .bind("user_id", userId)
            .mapTo<Timestamp>()
            .firstOrNull() ?: return null

        val currentTimeInSeconds = System.currentTimeMillis() / 1000
        val endDateInSeconds = endDate.time / 1000
        return (endDateInSeconds - currentTimeInSeconds).toInt()
    }

    /**
     * Method to delete a Cooldown
     */
    override fun deleteCooldownRequest(userId: Int) {
        handle.createUpdate(
            """
            DELETE FROM Cooldown
            WHERE user_id = :user_id
            """,
        )
            .bind("user_id", userId)
            .execute()
    }
}
