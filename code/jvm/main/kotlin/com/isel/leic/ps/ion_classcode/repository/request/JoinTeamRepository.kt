package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.JoinTeamInput
import com.isel.leic.ps.ion_classcode.domain.requests.JoinTeam


/**
 * Repository functions for Join Team Repository
 */
interface JoinTeamRepository {
    fun createJoinTeamRequest(request: JoinTeamInput,creator:Int): Int
    fun getJoinTeamRequests(): List<JoinTeam>
    fun getJoinTeamRequestById(id: Int): JoinTeam?
    fun getJoinTeamRequestsByUser(userId: Int): List<JoinTeam>
}
