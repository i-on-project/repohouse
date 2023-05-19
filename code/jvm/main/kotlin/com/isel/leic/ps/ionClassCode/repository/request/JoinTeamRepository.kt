package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.UserJoinTeam
import com.isel.leic.ps.ionClassCode.domain.input.request.JoinTeamInput
import com.isel.leic.ps.ionClassCode.domain.requests.JoinTeam

/**
 * Repository functions for Join Team Repository
 */
interface JoinTeamRepository {
    fun createJoinTeamRequest(request: JoinTeamInput, creator: Int): JoinTeam
    fun getJoinTeamRequests(): List<JoinTeam>
    fun getJoinTeamRequestById(id: Int): JoinTeam?
    fun getJoinTeamRequestsByUser(userId: Int): List<JoinTeam>
    fun getJoinTeamRequestByCompositeId(compositeId: Int): UserJoinTeam?
    fun updateJoinTeamState(requestId: Int, state: String)
}
