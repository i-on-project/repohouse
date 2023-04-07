package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.CreateRepoInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.request.JdbiCreateRepoRequestRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class JdbiCreateRepoRepositoryTests {
    @Test
    fun `createCreateRepoRequest should create a new createRepo request`() = testWithHandleAndRollback { handle ->
        val createRepoReq = JdbiCreateRepoRequestRepository(handle = handle)
        val request = CreateRepoInput(creator = 3)
        createRepoReq.createCreateRepoRequest(request = request)
    }

    @Test
    fun `getCreateRepoRequests should return all createRepo requests`() = testWithHandleAndRollback { handle ->
        val createRepoReq = JdbiCreateRepoRequestRepository(handle = handle)
        val requests = createRepoReq.getCreateRepoRequests()
        assert(requests.size == 2)
    }

    @Test
    fun `getCreateRepoRequestById should return the specific createRepo request`() = testWithHandleAndRollback { handle ->
        val createRepoReq = JdbiCreateRepoRequestRepository(handle = handle)
        val id = 5
        val creator = 4
        val request = createRepoReq.getCreateRepoRequestById(id = id) ?: fail("Request not found")
        assert(request.creator == creator)
    }

    @Test
    fun `getCreateRepoRequestsByUser should return createRepo requests for a user`() = testWithHandleAndRollback { handle ->
        val createRepoReq = JdbiCreateRepoRequestRepository(handle = handle)
        val userId = 4
        val requests = createRepoReq.getCreateRepoRequestsByUser(userId = userId)
        assert(requests.size == 2)
    }
}
