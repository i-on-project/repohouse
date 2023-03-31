package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Outbox
import com.isel.leic.ps.ion_classcode.domain.input.OutboxInput

interface OutboxRepository {

    fun createOutboxRequest(outbox: OutboxInput): Int? //
    fun getOutboxPendingRequests(): List<Outbox> //
    fun getOutboxRequest(outboxId: Int): Outbox? //
    fun updateOutboxStateRequest(outboxId: Int): Boolean //
    fun getOutboxRequestByUserId(userId: Int): Outbox? //
    fun deleteOutboxRequest(outboxId: Int): Boolean
}
