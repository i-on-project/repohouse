package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.ArchiveRepoInput
import com.isel.leic.ps.ion_classcode.domain.requests.ArchiveRepo

/**
 * Repository functions for Archive Repo Repository
 */
interface ArchiveRepoRequestRepository {
    fun createArchiveRepoRequest(request: ArchiveRepoInput,creator:Int): Int
    fun getArchiveRepoRequests(): List<ArchiveRepo>
    fun getArchiveRepoRequestById(id: Int): ArchiveRepo?
    fun getArchiveRepoRequestsByUser(userId: Int): List<ArchiveRepo>
}
