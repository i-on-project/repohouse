package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.*
import com.isel.leic.ps.ion_classcode.domain.requests.*

interface CreateTeamRepository {
    fun createCreateTeamRequest(request: CreateTeamInput): Int
    fun getCreateTeamRequests(): List<CreateTeam>
    fun getCreateTeamRequestById(id: Int): CreateTeam
    fun getCreateTeamRequestsByUser(userId: Int): List<CreateTeam>
}
