package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.LeaveTeamInputInterface
import com.isel.leic.ps.ion_classcode.domain.requests.LeaveTeam

interface LeaveTeamRepository {
    fun createLeaveTeamRequest(request: LeaveTeamInputInterface): Int
    fun getLeaveTeamRequests(): List<LeaveTeam>
    fun getLeaveTeamRequestById(id: Int): LeaveTeam?
    fun getLeaveTeamRequestsByUser(userId: Int): List<LeaveTeam>
}
