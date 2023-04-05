package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.CreateTeamInputInterface
import com.isel.leic.ps.ion_classcode.domain.requests.CreateTeam

interface CreateTeamRepository {
    fun createCreateTeamRequest(request: CreateTeamInputInterface): Int
    fun getCreateTeamRequests(): List<CreateTeam>
    fun getCreateTeamRequestById(id: Int): CreateTeam?
    fun getCreateTeamRequestsByUser(userId: Int): List<CreateTeam>
}
