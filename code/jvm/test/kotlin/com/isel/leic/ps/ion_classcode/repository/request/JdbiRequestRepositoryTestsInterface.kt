package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.RequestInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.request.JdbiRequestRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class JdbiRequestRepositoryTestsInterface {
    @Test
    fun `createRequest should create a new request`() = testWithHandleAndRollback { handle ->
        val requestRep = JdbiRequestRepository(handle = handle)
        val request = RequestInput(creator = 1)
        requestRep.createRequest(request = request)
    }

    @Test
    fun `getRequestById should return the specific request`() = testWithHandleAndRollback { handle ->
        val requestRep = JdbiRequestRepository(handle = handle)
        val id = 1
        val creator = 1
        val request = requestRep.getRequestById(id = id) ?: fail("Request not found")
        assert(request.creator == creator)
    }

    @Test
    fun `changeStatusRequest should change the status specific request`() = testWithHandleAndRollback { handle ->
        val requestRep = JdbiRequestRepository(handle = handle)
        val id = 1
        val state = "Rejected"
        requestRep.changeStateRequest(id = id, state = state)
        val request = requestRep.getRequestById(id = id) ?: fail("Request not found")
        assert(request.state == state)
    }

    @Test
    fun `getRequests should return all requests`() = testWithHandleAndRollback { handle ->
        val requestRep = JdbiRequestRepository(handle = handle)
        val requests = requestRep.getRequests()
        assert(requests.size == 30)
    }

    @Test
    fun `getRequestsByUser should return the requests for a user`() = testWithHandleAndRollback { handle ->
        val requestRep = JdbiRequestRepository(handle = handle)
        val userId = 1
        val requests = requestRep.getRequestsByUser(userId = userId)
        assert(requests.size == 2)
    }
}