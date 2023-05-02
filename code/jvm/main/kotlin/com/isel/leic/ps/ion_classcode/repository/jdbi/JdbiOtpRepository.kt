package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Otp
import com.isel.leic.ps.ion_classcode.domain.input.OtpInput
import com.isel.leic.ps.ion_classcode.repository.OtpRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.sql.Timestamp

const val INTERVAL = "10 MINUTES"

/**
 * Implementation of the Otp methods
 */
class JdbiOtpRepository(private val handle: Handle) : OtpRepository {
    override fun createOtpRequest(otp: OtpInput): Int? {
        return handle.createUpdate(
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
            .firstOrNull()
    }

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
