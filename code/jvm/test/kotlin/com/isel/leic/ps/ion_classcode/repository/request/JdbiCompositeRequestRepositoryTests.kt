package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.CompositeInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.request.JdbiCompositeRequestRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.request.JdbiRequestRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class JdbiCompositeRequestRepositoryTests {
    @Test
    fun `createCompositeRequest should create a new composite request`() = testWithHandleAndRollback { handle ->
        val compositeRep = JdbiCompositeRequestRepository(handle = handle)
        val requestRepo = JdbiRequestRepository(handle = handle)
        val request = CompositeInput(requests = listOf(1, 2), composite = null)
        val composite = compositeRep.createCompositeRequest(request = request, creator = 1)
        val r = requestRepo.getRequestById(id = 1) ?: fail("Request not found")
        val r1 = requestRepo.getRequestById(id = 2) ?: fail("Request not found")
        assert(composite.id == r.composite)
        assert(composite.id == r1.composite)
    }

    @Test
    fun `getCompositeRequestById should return the specific composite request`() = testWithHandleAndRollback { handle ->
        val compositeRep = JdbiCompositeRequestRepository(handle = handle)
        val id = 1
        val creator = 1
        val request = compositeRep.getCompositeRequestById(id = id) ?: fail("Request not found")
        assert(request.creator == creator)
    }

    @Test
    fun `changeStateCompositeRequest should change the status specific composite request`() = testWithHandleAndRollback { handle ->
        val compositeRep = JdbiCompositeRequestRepository(handle = handle)
        val id = 15
        val state = "Rejected"
        compositeRep.changeStateCompositeRequest(id = id, state = state)
        val request = compositeRep.getCompositeRequestById(id = id) ?: fail("Request not found")
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
        assert(requests.size == 3)
        assert(requests.first().requests.size == 2)
    }
}
