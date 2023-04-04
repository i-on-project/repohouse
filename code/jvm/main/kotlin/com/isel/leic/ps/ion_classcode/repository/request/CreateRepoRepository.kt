package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.CreateRepoInput
import com.isel.leic.ps.ion_classcode.domain.requests.CreateRepo

interface CreateRepoRepository {
    fun createCreateRepoRequest(request: CreateRepoInput): Int
    fun getCreateRepoRequests(): List<CreateRepo>
    fun getCreateRepoRequestById(id: Int): CreateRepo?
    fun getCreateRepoRequestsByUser(userId: Int): List<CreateRepo>
}
