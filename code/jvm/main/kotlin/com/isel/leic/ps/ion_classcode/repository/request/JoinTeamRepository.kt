package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.*
import com.isel.leic.ps.ion_classcode.domain.requests.*

interface JoinTeamRepository {
    fun createJoinTeamRequest(request: JoinTeamInput): Int
    fun getJoinTeamRequests(): List<JoinTeam>
    fun getJoinTeamRequestById(id: Int): JoinTeam
    fun getJoinTeamRequestsByUser(userId: Int): List<JoinTeam>
}
