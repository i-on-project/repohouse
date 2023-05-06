package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Outbox
import com.isel.leic.ps.ion_classcode.domain.input.OutboxInput

/**
 * Repository functions for Outbox Repository
 */
interface OutboxRepository {
    fun createOutboxRequest(outbox: OutboxInput): Outbox
    fun getOutboxPendingRequests(): List<Outbox>
    fun getOutboxRequest(userId: Int): Outbox?
    fun updateOutboxStateRequest(userId: Int,state:String)
    fun updateOutboxSentTimeRequest(userId: Int)
    fun deleteOutboxRequest(userId: Int)
}
