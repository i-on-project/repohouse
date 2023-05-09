package com.isel.leic.ps.ionClassCode.repository

import com.isel.leic.ps.ionClassCode.domain.input.ApplyInput
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiApplyRepository
import com.isel.leic.ps.ionClassCode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test


class ApplyRepositoryTests {

    @Test
    fun `createApplyRequest should create a new apply request`() = testWithHandleAndRollback { handle ->
        val applyReq = JdbiApplyRepository(handle = handle)
        val request = ApplyInput(pendingTeacherId = 4)
        val created = applyReq.createApplyRequest(request = request)
        val apply = applyReq.getApplyRequestById(id = created.id)
        assert(apply != null)
    }

    @Test
    fun `getApplyRequests should return all apply requests`() = testWithHandleAndRollback { handle ->
        val applyReq = JdbiApplyRepository(handle = handle)
        val requests = applyReq.getApplyRequests()
        assert(requests.size == 2)
    }

    @Test
    fun `getApplyRequestById should return the specific apply request`() = testWithHandleAndRollback { handle ->
        val applyReq = JdbiApplyRepository(handle = handle)
        val id = 1
        val creator = 1
        val request = applyReq.getApplyRequestById(id = id) ?: fail("Request not found")
        assert(request.pendingTeacherId == creator)
    }

    @Test
    fun `getApplyRequestsByUser should return apply requests for a teacher`() = testWithHandleAndRollback { handle ->
        val applyReq = JdbiApplyRepository(handle = handle)
        val teacherId = 1
        val requests = applyReq.getApplyRequestsByUser(teacherId = teacherId)
        assert(requests.size == 2)
    }
}
