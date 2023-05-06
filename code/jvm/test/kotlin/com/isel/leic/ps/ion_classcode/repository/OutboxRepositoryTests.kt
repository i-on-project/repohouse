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
        val created = outboxRepo.createOutboxRequest(outbox = OutboxInput(userId = 2))
        val outbox = outboxRepo.getOutboxRequest(userId = created.userId)
        assert(outbox != null)
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
        val userId = 3
        outboxRepo.getOutboxRequest(userId = userId) ?: fail("Request not found")
    }

    @Test
    fun `can update a request status`() = testWithHandleAndRollback { handle ->
        val outboxRepo = JdbiOutboxRepository(handle = handle)
        val userId = 3
        val status = "Sent"
        outboxRepo.updateOutboxStateRequest(userId = userId, state =status)
        val request = outboxRepo.getOutboxRequest(userId = userId) ?: fail("Request not found")
        assert(request.status == status)
    }

    @Test
    fun `can update a request sent time`() = testWithHandleAndRollback { handle ->
        val outboxRepo = JdbiOutboxRepository(handle = handle)
        val userId = 3
        val request = outboxRepo.getOutboxRequest(userId = userId) ?: fail("Request not found")
        outboxRepo.updateOutboxSentTimeRequest(userId = userId)
        val outbox = outboxRepo.getOutboxRequest(userId = userId) ?: fail("Request not found")
        assert(request.sentAt != outbox.sentAt)
    }

    @Test
    fun `can delete a request`() = testWithHandleAndRollback { handle ->
        val outboxRepo = JdbiOutboxRepository(handle = handle)
        val userId = 3
        outboxRepo.deleteOutboxRequest(userId = userId)
        val request = outboxRepo.getOutboxRequest(userId = userId)
        assert(request == null)
    }
}
