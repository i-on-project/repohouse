package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.input.request.CreateRepoInput
import com.isel.leic.ps.ionClassCode.repository.jdbi.request.JdbiCreateRepoRequestRepository
import com.isel.leic.ps.ionClassCode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class JdbiCreateRepoRepositoryTests {

    @Test
    fun `createCreateRepoRequest should create a new createRepo request`() = testWithHandleAndRollback { handle ->
        val createRepoReq = JdbiCreateRepoRequestRepository(handle = handle)
        val request = CreateRepoInput(repoId = 3, composite = 15)
        createRepoReq.createCreateRepoRequest(request = request, creator = 3)
    }

    @Test
    fun `getCreateRepoRequests should return all createRepo requests`() = testWithHandleAndRollback { handle ->
        val createRepoReq = JdbiCreateRepoRequestRepository(handle = handle)
        val requests = createRepoReq.getCreateRepoRequests()
        assert(requests.size == 1)
    }

    @Test
    fun `getCreateRepoRequestById should return the specific createRepo request`() = testWithHandleAndRollback { handle ->
        val createRepoReq = JdbiCreateRepoRequestRepository(handle = handle)
        val id = 5
        val creator = 3
        val request = createRepoReq.getCreateRepoRequestById(id = id) ?: fail("Request not found")
        assert(request.creator == creator)
    }

    @Test
    fun `getCreateRepoRequestsByUser should return createRepo requests for a user`() = testWithHandleAndRollback { handle ->
        val createRepoReq = JdbiCreateRepoRequestRepository(handle = handle)
        val userId = 3
        val requests = createRepoReq.getCreateRepoRequestsByUser(userId = userId)
        assert(requests.size == 1)
    }

    @Test
    fun `getCreateRepoRequestByCompositeId should return createRepo requests for a composite`() = testWithHandleAndRollback { handle ->
        val createRepoReq = JdbiCreateRepoRequestRepository(handle = handle)
        val compositeId = 15
        val request = createRepoReq.getCreateRepoRequestByCompositeId(compositeId = compositeId)
        assert(request?.id == 5)
    }
}
