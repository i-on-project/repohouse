package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.JoinTeamInputInterface
import com.isel.leic.ps.ion_classcode.domain.requests.JoinTeam

interface JoinTeamRepository {
    fun createJoinTeamRequest(request: JoinTeamInputInterface): Int
    fun getJoinTeamRequests(): List<JoinTeam>
    fun getJoinTeamRequestById(id: Int): JoinTeam?
    fun getJoinTeamRequestsByUser(userId: Int): List<JoinTeam>
}
