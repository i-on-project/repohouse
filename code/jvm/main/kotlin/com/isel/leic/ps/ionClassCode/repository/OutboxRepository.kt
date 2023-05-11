package com.isel.leic.ps.ionClassCode.repository

import com.isel.leic.ps.ionClassCode.domain.Outbox
import com.isel.leic.ps.ionClassCode.domain.input.OutboxInput

/**
 * Repository functions for Outbox Repository
 */
interface OutboxRepository {
    fun createOutboxRequest(outbox: OutboxInput): Outbox
    fun getOutboxPendingRequests(): List<Outbox>
    fun getOutboxRequest(userId: Int): Outbox?
    fun updateOutboxStateRequest(userId: Int, state: String)
    fun updateOutboxSentTimeRequest(userId: Int)
    fun deleteOutboxRequest(userId: Int)
}
