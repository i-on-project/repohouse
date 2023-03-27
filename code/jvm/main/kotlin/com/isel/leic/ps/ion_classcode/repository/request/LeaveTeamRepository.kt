package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.*
import com.isel.leic.ps.ion_classcode.domain.requests.*

interface LeaveTeamRepository {
    fun createLeaveTeamRequest(request: LeaveTeamInput): Int
    fun getLeaveTeamRequests(): List<LeaveTeam>
    fun getLeaveTeamRequestById(id: Int): LeaveTeam
    fun getLeaveTeamRequestsByUser(userId: Int): List<LeaveTeam>
}
