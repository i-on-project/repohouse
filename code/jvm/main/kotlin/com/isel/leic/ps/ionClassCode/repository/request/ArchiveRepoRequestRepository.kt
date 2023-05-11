package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.input.request.ArchiveRepoInput
import com.isel.leic.ps.ionClassCode.domain.requests.ArchiveRepo

/**
 * Repository functions for Archive Repo Repository
 */
interface ArchiveRepoRequestRepository {
    fun createArchiveRepoRequest(request: ArchiveRepoInput, creator: Int): ArchiveRepo
    fun getArchiveRepoRequests(): List<ArchiveRepo>
    fun getArchiveRepoRequestById(id: Int): ArchiveRepo?
    fun getArchiveRepoRequestsByUser(userId: Int): List<ArchiveRepo>
}
