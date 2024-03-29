package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveCourseInput
import com.isel.leic.ps.ionClassCode.repository.jdbi.request.JdbiLeaveCourseRequestRepository
import com.isel.leic.ps.ionClassCode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class JdbiLeaveCourseInputRepositoryTests {

    @Test
    fun `createLeaveCourseRequest should create a new leaveCourse request`() = testWithHandleAndRollback { handle ->
        val leaveCourseReq = JdbiLeaveCourseRequestRepository(handle = handle)
        val request = LeaveCourseInput(courseId = 1, composite = 16, githubUsername = "test")
        leaveCourseReq.createLeaveCourseRequest(request = request, creator = 3)
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
        val creator = 4
        val request = leaveCourseReq.getLeaveCourseRequestById(id = id) ?: fail("Request not found")
        assert(request.creator == creator)
    }

    @Test
    fun `getLeaveCourseRequestsByUser should return leaveCourse requests for a user`() = testWithHandleAndRollback { handle ->
        val leaveCourseReq = JdbiLeaveCourseRequestRepository(handle = handle)
        val userId = 4
        val requests = leaveCourseReq.getLeaveCourseRequestsByUser(userId = userId)
        assert(requests.size == 2)
    }
}
