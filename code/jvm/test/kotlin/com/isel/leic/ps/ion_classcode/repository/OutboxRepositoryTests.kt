package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.input.OutboxInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiOutboxRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class OutboxRepositoryTests {
    @Test
    fun `can create an outbox`() = testWithHandleAndRollback { handle ->
        val outboxRepo = JdbiOutboxRepository(handle = handle)
        outboxRepo.createOutboxRequest(outbox = OutboxInput(userId = 2, otp = 123))
    }

    @Test
    fun `can get all pending requests`() = testWithHandleAndRollback { handle ->
        val outboxRepo = JdbiOutboxRepository(handle = handle)
        val list = outboxRepo.getOutboxPendingRequests()
        assert(list.size == 2)
    }

    @Test
    fun `can get a request`() = testWithHandleAndRollback { handle ->
        val outboxRepo = JdbiOutboxRepository(handle = handle)
        val outboxId = 1
        val otp = 123456
        val request = outboxRepo.getOutboxRequest(outboxId = outboxId) ?: fail("Request not found")
        assert(request.otp == otp)
    }

    @Test
    fun `can update a request`() = testWithHandleAndRollback { handle ->
        val outboxRepo = JdbiOutboxRepository(handle = handle)
        val outboxId = 1
        val status = "Sent"
        val result = outboxRepo.updateOutboxStateRequest(outboxId = outboxId)
        assert(result)
        val request = outboxRepo.getOutboxRequest(outboxId = outboxId) ?: fail("Request not found")
        assert(request.status == status)
    }

    @Test
    fun `can get a request by user id`() = testWithHandleAndRollback { handle ->
        val outboxRepo = JdbiOutboxRepository(handle = handle)
        val userId = 1
        val otp = 123456
        val request = outboxRepo.getOutboxRequestByUserId(userId = userId) ?: fail("Request not found")
        assert(request.otp == otp)
    }

    @Test
    fun `can delete a request`() = testWithHandleAndRollback { handle ->
        val outboxRepo = JdbiOutboxRepository(handle = handle)
        val outboxId = 1
        val result = outboxRepo.deleteOutboxRequest(outboxId = outboxId)
        assert(result)
        val request = outboxRepo.getOutboxRequest(outboxId = outboxId)
        assert(request == null)
    }
}
