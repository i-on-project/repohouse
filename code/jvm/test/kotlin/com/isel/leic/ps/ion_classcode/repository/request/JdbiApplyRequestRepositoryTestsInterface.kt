package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.ApplyInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.request.JdbiApplyRequestRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class JdbiApplyRequestRepositoryTestsInterface {
    @Test
    fun `createApplyRequest should create a new apply request`() = testWithHandleAndRollback { handle ->
        val applyReq = JdbiApplyRequestRepository(handle = handle)
        val request = ApplyInput(creator = 3)
        applyReq.createApplyRequest(request = request)
    }

    @Test
    fun `getApplyRequests should return all apply requests`() = testWithHandleAndRollback { handle ->
        val applyReq = JdbiApplyRequestRepository(handle = handle)
        val requests = applyReq.getApplyRequests()
        assert(requests.size == 2)
    }

    @Test
    fun `getApplyRequestById should return the specific apply request`() = testWithHandleAndRollback { handle ->
        val applyReq = JdbiApplyRequestRepository(handle = handle)
        val id = 1
        val creator = 1
        val request = applyReq.getApplyRequestById(id = id) ?: fail("Request not found")
        assert(request.creator == creator)
    }

    @Test
    fun `getApplyRequestsByUser should return apply requests for a teacher`() = testWithHandleAndRollback { handle ->
        val applyReq = JdbiApplyRequestRepository(handle = handle)
        val teacherId = 1
        val requests = applyReq.getApplyRequestsByUser(teacherId = teacherId)
        assert(requests.size == 2)
    }
}
