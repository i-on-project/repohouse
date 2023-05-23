package com.isel.leic.ps.ionClassCode.repository.jdbi

import com.isel.leic.ps.ionClassCode.domain.Otp
import com.isel.leic.ps.ionClassCode.domain.input.OtpInput
import com.isel.leic.ps.ionClassCode.repository.OtpRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.sql.Timestamp

const val INTERVAL = "10 MINUTES"

/**
 * Implementation of the Otp methods
 */
class JdbiOtpRepository(private val handle: Handle) : OtpRepository {

    /**
     * Method to create an Otp Request
     */
    override fun createOtpRequest(otp: OtpInput): Otp {
        handle.createUpdate(
            """
            INSERT INTO Otp (user_id, otp, expired_at,tries)
            VALUES (:user_id,:otp,:expired_at,0)
            RETURNING user_id
            """,
        )
            .bind("user_id", otp.userId)
            .bind("otp", otp.otp)
            .bind("expired_at", toTimestamp())
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
        return Otp(otp.userId, otp.otp, toTimestamp(), 0)
    }

    /**
     * Method to get an Otp Request by user id
     */
    override fun getOtpRequest(userId: Int): Otp? {
        return handle.createQuery(
            """
            SELECT * FROM Otp
            WHERE user_id = :id
            """,
        )
            .bind("id", userId)
            .mapTo<Otp>()
            .firstOrNull()
    }

    /**
     * Method to add a try to an Otp Request
     */
    override fun addTryToOtpRequest(userId: Int, tries: Int): Boolean {
        return handle.createUpdate(
            """
            UPDATE Otp
            SET tries = tries + 1
            WHERE user_id = :id and tries = :tries
            """,
        )
            .bind("id", userId)
            .bind("tries", tries)
            .execute() > 0
    }

    /**
     * Method to delete an Otp Request
     */
    override fun deleteOtpRequest(userId: Int) {
        handle.createUpdate(
            """
            DELETE FROM Otp
            WHERE user_id = :id
            """,
        )
            .bind("id", userId)
            .execute()
    }

    /**
     * Method to create a query for add to timestamp
     */
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
