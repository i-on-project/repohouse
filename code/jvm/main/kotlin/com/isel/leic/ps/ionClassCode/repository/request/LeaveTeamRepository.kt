package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveTeamInput
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveTeam
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveTeamWithRepoName

/**
 * Repository functions for Leave Team Repository
 */
interface LeaveTeamRepository {
    fun createLeaveTeamRequest(request: LeaveTeamInput, creator: Int): LeaveTeam
    fun getLeaveTeamRequests(): List<LeaveTeam>
    fun getLeaveTeamWithRepoNameRequests(teamId: Int): List<LeaveTeamWithRepoName>
    fun getLeaveTeamRequestById(id: Int): LeaveTeam?
    fun getLeaveTeamRequestsByCompositeId(compositeId: Int): List<LeaveTeam>
    fun updateLeaveTeamState(requestId: Int, state: String)
}
