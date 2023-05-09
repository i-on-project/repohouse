package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.input.request.CreateTeamInput
import com.isel.leic.ps.ionClassCode.repository.jdbi.request.JdbiCreateTeamRequestRepository
import com.isel.leic.ps.ionClassCode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class JdbiCreateTeamRequestRepositoryTestsInterface {

    @Test
    fun `createCreateTeamRequest should create a new createTeam request`() = testWithHandleAndRollback { handle ->
        val createTeamReq = JdbiCreateTeamRequestRepository(handle = handle)
        val request = CreateTeamInput()
        createTeamReq.createCreateTeamRequest(request = request, creator = 3)
    }

    @Test
    fun `getCreateTeamRequests should return all createTeams requests`() = testWithHandleAndRollback { handle ->
        val createTeamReq = JdbiCreateTeamRequestRepository(handle = handle)
        val requests = createTeamReq.getCreateTeamRequests()
        assert(requests.size == 2)
    }

    @Test
    fun `getCreateTeamRequestById should return the specific createTeams request`() = testWithHandleAndRollback { handle ->
        val createTeamReq = JdbiCreateTeamRequestRepository(handle = handle)
        val id = 7
        val creator = 4
        val request = createTeamReq.getCreateTeamRequestById(id = id) ?: fail("Request not found")
        assert(request.creator == creator)
    }

    @Test
    fun `getCreateTeamRequestsByUser should return createTeams requests for a user`() = testWithHandleAndRollback { handle ->
        val createTeamReq = JdbiCreateTeamRequestRepository(handle = handle)
        val userId = 4
        val requests = createTeamReq.getCreateTeamRequestsByUser(userId = userId)
        assert(requests.size == 2)
    }
}
