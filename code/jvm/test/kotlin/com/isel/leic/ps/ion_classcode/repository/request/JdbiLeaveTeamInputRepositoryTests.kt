package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.LeaveTeamInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.request.JdbiLeaveTeamRequestRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class JdbiLeaveTeamInputRepositoryTests {
    @Test
    fun `createLeaveTeamRequest should create a new leaveTeam request`() = testWithHandleAndRollback { handle ->
        val leaveTeamReq = JdbiLeaveTeamRequestRepository(handle = handle)
        val request = LeaveTeamInput(teamId = 1, creator = 3)
        leaveTeamReq.createLeaveTeamRequest(request = request)
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
        val creator = 4
        val request = leaveTeamReq.getLeaveTeamRequestById(id = id) ?: fail("Request not found")
        assert(request.creator == creator)
    }

    @Test
    fun `getLeaveTeamRequestsByUser should return leaveTeam requests for a user`() = testWithHandleAndRollback { handle ->
        val leaveTeamReq = JdbiLeaveTeamRequestRepository(handle = handle)
        val userId = 4
        val requests = leaveTeamReq.getLeaveTeamRequestsByUser(userId = userId)
        assert(requests.size == 2)
    }
}
