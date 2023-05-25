package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveTeamInput
import com.isel.leic.ps.ionClassCode.repository.jdbi.request.JdbiLeaveTeamRequestRepository
import com.isel.leic.ps.ionClassCode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class JdbiLeaveTeamInputRepositoryTests {

    @Test
    fun `createLeaveTeamRequest should create a new leaveTeam request`() = testWithHandleAndRollback { handle ->
        val leaveTeamReq = JdbiLeaveTeamRequestRepository(handle = handle)
        val request = LeaveTeamInput(teamId = 1)
        leaveTeamReq.createLeaveTeamRequest(request = request, creator = 3)
    }

    @Test
    fun `getLeaveTeamRequests should return all leaveTeam requests`() = testWithHandleAndRollback { handle ->
        val leaveTeamReq = JdbiLeaveTeamRequestRepository(handle = handle)
        val requests = leaveTeamReq.getLeaveTeamRequests()
        assert(requests.size == 2)
    }

    @Test
    fun `getLeaveTeamRequestById should return the specific leaveTeam request`() = testWithHandleAndRollback { handle ->
        val leaveTeamReq = JdbiLeaveTeamRequestRepository(handle = handle)
        val id = 13
        val creator = 5
        val request = leaveTeamReq.getLeaveTeamRequestById(id = id) ?: fail("Request not found")
        assert(request.creator == creator)
    }
}
