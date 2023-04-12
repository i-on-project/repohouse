package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.requests.LeaveTeam


/**
 * Repository functions for Leave Team Repository
 */
interface LeaveTeamRepository {
    fun createLeaveTeamRequest(request: com.isel.leic.ps.ion_classcode.domain.input.request.LeaveTeamInput): Int
    fun getLeaveTeamRequests(): List<LeaveTeam>
    fun getLeaveTeamRequestById(id: Int): LeaveTeam?
    fun getLeaveTeamRequestsByUser(userId: Int): List<LeaveTeam>
}
