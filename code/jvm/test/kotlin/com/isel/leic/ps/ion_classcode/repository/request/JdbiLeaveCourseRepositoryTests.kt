package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.LeaveCourseInputInterface
import com.isel.leic.ps.ion_classcode.repository.jdbi.request.JdbiLeaveCourseRequestRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class JdbiLeaveCourseRepositoryTests {
    @Test
    fun `createLeaveCourseRequest should create a new leaveCourse request`() = testWithHandleAndRollback { handle ->
        val leaveCourseReq = JdbiLeaveCourseRequestRepository(handle = handle)
        val request = LeaveCourseInputInterface(courseId = 1, creator = 3)
        leaveCourseReq.createLeaveCourseRequest(request = request)
    }

    @Test
    fun `getLeaveCourseRequests should return all leaveCourse requests`() = testWithHandleAndRollback { handle ->
        val leaveCourseReq = JdbiLeaveCourseRequestRepository(handle = handle)
        val requests = leaveCourseReq.getLeaveCourseRequests()
        assert(requests.size == 2)
    }

    @Test
    fun `getLeaveCourseRequestById should return the specific leaveCourse request`() = testWithHandleAndRollback { handle ->
        val leaveCourseReq = JdbiLeaveCourseRequestRepository(handle = handle)
        val id = 11
        val creator = 5
        val request = leaveCourseReq.getLeaveCourseRequestById(id = id) ?: fail("Request not found")
        assert(request.creator == creator)
    }

    @Test
    fun `getLeaveCourseRequestsByUser should return leaveCourse requests for a user`() = testWithHandleAndRollback { handle ->
        val leaveCourseReq = JdbiLeaveCourseRequestRepository(handle = handle)
        val userId = 5
        val requests = leaveCourseReq.getLeaveCourseRequestsByUser(userId = userId)
        assert(requests.size == 2)
    }
}
