package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.JoinTeamInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.request.JdbiJoinTeamRequestRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class JoinTeamRepositoryTests {
    @Test
    fun `createJoinTeamRequest should create a new joinTeam request`() = testWithHandleAndRollback { handle ->
        val joinTeamReq = JdbiJoinTeamRequestRepository(handle = handle)
        val request = JoinTeamInput(teamId = 1, creator = 3, assignmentId = 1)
        joinTeamReq.createJoinTeamRequest(request = request)
    }

    @Test
    fun `getJoinTeamRequests should return all joinTeam requests`() = testWithHandleAndRollback { handle ->
        val joinTeamReq = JdbiJoinTeamRequestRepository(handle = handle)
        val requests = joinTeamReq.getJoinTeamRequests()
        assert(requests.size == 2)
    }

    @Test
    fun `getJoinTeamRequestById should return the specific joinTeam request`() = testWithHandleAndRollback { handle ->
        val joinTeamReq = JdbiJoinTeamRequestRepository(handle = handle)
        val id = 9
        val creator = 4
        val request = joinTeamReq.getJoinTeamRequestById(id = id) ?: fail("Request not found")
        assert(request.creator == creator)
    }

    @Test
    fun `getJoinTeamRequestsByUser should return joinTeam requests for a user`() = testWithHandleAndRollback { handle ->
        val joinTeamReq = JdbiJoinTeamRequestRepository(handle = handle)
        val userId = 4
        val requests = joinTeamReq.getJoinTeamRequestsByUser(userId = userId)
        assert(requests.size == 2)
    }
}
