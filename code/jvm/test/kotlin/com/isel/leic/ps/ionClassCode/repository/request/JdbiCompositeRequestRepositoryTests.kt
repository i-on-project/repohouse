package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.input.request.CompositeInput
import com.isel.leic.ps.ionClassCode.repository.jdbi.request.JdbiCompositeRequestRepository
import com.isel.leic.ps.ionClassCode.repository.jdbi.request.JdbiRequestRepository
import com.isel.leic.ps.ionClassCode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class JdbiCompositeRequestRepositoryTests {

    @Test
    fun `createCompositeRequest should create a new composite request`() = testWithHandleAndRollback { handle ->
        val compositeRep = JdbiCompositeRequestRepository(handle = handle)
        val request = CompositeInput(composite = null)
        compositeRep.createCompositeRequest(request = request, creator = 1)
    }

    @Test
    fun `getCompositeRequestById should return the list of ids`() = testWithHandleAndRollback { handle ->
        val compositeRep = JdbiCompositeRequestRepository(handle = handle)
        val compositeId = 15
        val request = compositeRep.getCompositeRequestsById(compositeId = compositeId) ?: fail("Request not found")
        assert(request.size == 3)
    }

    @Test
    fun `changeStateCompositeRequest should change the status specific composite request`() = testWithHandleAndRollback { handle ->
        val compositeRep = JdbiCompositeRequestRepository(handle = handle)
        val requestRepo = JdbiRequestRepository(handle = handle)
        val id = 15
        val state = "Rejected"
        compositeRep.updateCompositeState(requestId = id, state = state)
        val request = requestRepo.getRequestById(id = id) ?: fail("Request not found")
        assert(request.state == state)
    }

    @Test
    fun `getCompositeRequests should return all composite requests`() = testWithHandleAndRollback { handle ->
        val compositeRep = JdbiCompositeRequestRepository(handle = handle)
        val requests = compositeRep.getCompositeRequests()
        assert(requests.size == 6)
    }

    @Test
    fun `getCompositeRequestsByUser should return the composite requests for a user`() = testWithHandleAndRollback { handle ->
        val compositeRep = JdbiCompositeRequestRepository(handle = handle)
        val userId = 5
        val requests = compositeRep.getCompositeRequestsByUser(userId = userId)
        assert(requests.size == 2)
    }
}
