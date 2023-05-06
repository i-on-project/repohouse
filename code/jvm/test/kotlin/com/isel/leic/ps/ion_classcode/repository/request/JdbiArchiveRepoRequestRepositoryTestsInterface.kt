package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.ArchiveRepoInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.request.JdbiArchiveRepoRequestRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class JdbiArchiveRepoRequestRepositoryTestsInterface {

    @Test
    fun `createArchiveRepoRequest should create a new archiveRepo request`() = testWithHandleAndRollback { handle ->
        val archiveReq = JdbiArchiveRepoRequestRepository(handle = handle)
        val request = ArchiveRepoInput(repoId = 1, composite = null)
        val created = archiveReq.createArchiveRepoRequest(request = request, creator = 3)
        val archive = archiveReq.getArchiveRepoRequestById(id = created.id)
        assert(archive != null)
    }

    @Test
    fun `getArchiveRepoRequests should return all archiveRepo requests`() = testWithHandleAndRollback { handle ->
        val archiveReq = JdbiArchiveRepoRequestRepository(handle = handle)
        val requests = archiveReq.getArchiveRepoRequests()
        assert(requests.size == 2)
    }

    @Test
    fun `getArchiveRepoRequestById should return the specific archiveRepo request`() = testWithHandleAndRollback { handle ->
        val archiveReq = JdbiArchiveRepoRequestRepository(handle = handle)
        val id = 3
        val creator = 3
        val request = archiveReq.getArchiveRepoRequestById(id = id) ?: fail("Request not found")
        assert(request.creator == creator)
    }

    @Test
    fun `getArchiveRepoRequestsByUser should return archiveRepo requests for a user`() = testWithHandleAndRollback { handle ->
        val archiveReq = JdbiArchiveRepoRequestRepository(handle = handle)
        val userId = 3
        val requests = archiveReq.getArchiveRepoRequestsByUser(userId = userId)
        assert(requests.size == 2)
    }
}
