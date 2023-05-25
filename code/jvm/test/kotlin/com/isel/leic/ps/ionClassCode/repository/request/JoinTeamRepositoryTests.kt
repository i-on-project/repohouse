package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.input.request.JoinTeamInput
import com.isel.leic.ps.ionClassCode.repository.jdbi.request.JdbiJoinTeamRequestRepository
import com.isel.leic.ps.ionClassCode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class JoinTeamRepositoryTests {

    @Test
    fun `createJoinTeamRequest should create a new joinTeam request`() = testWithHandleAndRollback { handle ->
        val joinTeamReq = JdbiJoinTeamRequestRepository(handle = handle)
        val request = JoinTeamInput(teamId = 1, assignmentId = 1, creatorGitHubUserName = "test")
        joinTeamReq.createJoinTeamRequest(request = request, creator = 3,)
    }

    @Test
    fun `getJoinTeamRequests should return all joinTeam requests`() = testWithHandleAndRollback { handle ->
        val joinTeamReq = JdbiJoinTeamRequestRepository(handle = handle)
        val requests = joinTeamReq.getJoinTeamRequests()
        assert(requests.size == 1)
    }

    @Test
    fun `getJoinTeamRequestById should return the specific joinTeam request`() = testWithHandleAndRollback { handle ->
        val joinTeamReq = JdbiJoinTeamRequestRepository(handle = handle)
        val id = 9
        val creator = 5
        val request = joinTeamReq.getJoinTeamRequestById(id = id) ?: fail("Request not found")
        assert(request.creator == creator)
    }


    @Test
    fun `getJoinTeamRequestByCompositeId should return joinTeam requests for a user`() = testWithHandleAndRollback { handle ->
        val joinTeamReq = JdbiJoinTeamRequestRepository(handle = handle)
        val compositeId = 15
        val request = joinTeamReq.getJoinTeamRequestByCompositeId(compositeId = compositeId)
        assert(request?.id == 9)
    }
}
