package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.ArchieveRepoInput
import com.isel.leic.ps.ion_classcode.domain.requests.ArchieveRepo

interface ArchieveRepoRequestRepository {
    fun createArchieveRepoRequest(request: ArchieveRepoInput): Int
    fun getArchieveRepoRequests(): List<ArchieveRepo>
    fun getArchieveRepoRequestById(id: Int): ArchieveRepo
    fun getArchieveRepoRequestsByUser(userId: Int): List<ArchieveRepo>
}
