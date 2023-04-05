package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.ArchiveRepoInputInterface
import com.isel.leic.ps.ion_classcode.domain.requests.ArchiveRepo

interface ArchiveRepoRequestRepository {
    fun createArchiveRepoRequest(request: ArchiveRepoInputInterface): Int
    fun getArchiveRepoRequests(): List<ArchiveRepo>
    fun getArchiveRepoRequestById(id: Int): ArchiveRepo?
    fun getArchiveRepoRequestsByUser(userId: Int): List<ArchiveRepo>
}
