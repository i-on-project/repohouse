package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.Team
import com.isel.leic.ps.ionClassCode.domain.input.request.CreateTeamInput
import com.isel.leic.ps.ionClassCode.domain.requests.CreateTeam

/**
 * Repository functions for Create Team Repository
 */
interface CreateTeamRepository {
    fun createCreateTeamRequest(request: CreateTeamInput, creator: Int): CreateTeam
    fun getCreateTeamRequests(): List<CreateTeam>
    fun getCreateTeamRequestById(id: Int): CreateTeam?
    fun getCreateTeamRequests(teamIds: List<Team>): List<CreateTeam>
    fun getCreateTeamRequestByCompositeId(compositeId: Int): CreateTeam?
    fun getCreateTeamRequestByTeamId(teamId: Int): CreateTeam?
    fun updateCreateTeamRequestState(requestId: Int, state: String, githubTeamId: Int?)
}
