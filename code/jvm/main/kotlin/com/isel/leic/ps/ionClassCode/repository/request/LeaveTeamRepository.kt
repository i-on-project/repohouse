package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveTeamInput
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveTeam

/**
 * Repository functions for Leave Team Repository
 */
interface LeaveTeamRepository {
    fun createLeaveTeamRequest(request: LeaveTeamInput, creator: Int): LeaveTeam
    fun getLeaveTeamRequests(): List<LeaveTeam>
    fun getLeaveTeamRequestById(id: Int): LeaveTeam?
    fun updateLeaveTeamState(requestId: Int, state: String)

}
