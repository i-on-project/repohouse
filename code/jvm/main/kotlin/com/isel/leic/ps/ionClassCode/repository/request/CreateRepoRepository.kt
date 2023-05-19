package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.RepoNotCreated
import com.isel.leic.ps.ionClassCode.domain.input.request.CreateRepoInput
import com.isel.leic.ps.ionClassCode.domain.requests.CreateRepo

/**
 * Repository functions for Create Repo Repository
 */
interface CreateRepoRepository {
    fun createCreateRepoRequest(request: CreateRepoInput, creator: Int): CreateRepo
    fun getCreateRepoRequests(): List<CreateRepo>
    fun getCreateRepoRequestById(id: Int): CreateRepo?
    fun getCreateRepoRequestsByUser(userId: Int): List<CreateRepo>
    fun getCreateRepoRequestByCompositeId(compositeId: Int): RepoNotCreated?
    fun updateCreateRepoState(requestId: Int, state: String)
}
