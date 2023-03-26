package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.input.AssigmentInput
import com.isel.leic.ps.ion_classcode.domain.input.FeedbackInput
import com.isel.leic.ps.ion_classcode.domain.input.request.ApplyInput
import com.isel.leic.ps.ion_classcode.domain.input.request.ArchieveRepoInput
import com.isel.leic.ps.ion_classcode.domain.input.request.CreateRepoInput
import com.isel.leic.ps.ion_classcode.domain.input.request.RequestInput
import com.isel.leic.ps.ion_classcode.domain.requests.Apply
import com.isel.leic.ps.ion_classcode.domain.requests.ArchieveRepo
import com.isel.leic.ps.ion_classcode.domain.requests.CreateRepo
import com.isel.leic.ps.ion_classcode.domain.requests.Request

interface CreateRepoRepository {
    fun createCreateRepoRequest(request: CreateRepoInput): Int
    fun getCreateRepoRequests(): List<CreateRepo>
    fun getCreateRepoRequestById(id: Int): CreateRepo
    fun getCreateRepoRequestsByUser(userId: Int): List<CreateRepo>

}
