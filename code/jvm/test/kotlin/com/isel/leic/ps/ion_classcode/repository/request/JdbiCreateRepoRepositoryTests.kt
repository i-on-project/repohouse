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
        val request = CreateRepoInput(repoId = 1, creator = 3)
        createRepoReq.createCreateRepoRequest(request = request)
    }

    @Test
    fun `getArchiveRepoRequests should return all archiveRepo requests`() = testWithHandleAndRollback { handle ->
        val createRepoReq = JdbiCreateRepoRequestRepository(handle = handle)
        val requests = createRepoReq.getCreateRepoRequests()
        assert(requests.size == 2)
    }

    @Test
    fun `getArchiveRepoRequestById should return the specific archiveRepo request`() = testWithHandleAndRollback { handle ->
        val createRepoReq = JdbiCreateRepoRequestRepository(handle = handle)
        val id = 5
        val creator = 4
        val request = createRepoReq.getCreateRepoRequestById(id = id) ?: fail("Request not found")
        assert(request.creator == creator)
    }

    @Test
    fun `getArchiveRepoRequestsByUser should return apply requests for a user`() = testWithHandleAndRollback { handle ->
        val createRepoReq = JdbiCreateRepoRequestRepository(handle = handle)
        val userId = 4
        val requests = createRepoReq.getCreateRepoRequestsByUser(userId = userId)
        assert(requests.size == 2)
    }
}