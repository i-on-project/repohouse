package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.input.AssigmentInput
import com.isel.leic.ps.ion_classcode.domain.input.FeedbackInput
import com.isel.leic.ps.ion_classcode.domain.input.request.ApplyInput
import com.isel.leic.ps.ion_classcode.domain.input.request.ArchieveRepoInput
import com.isel.leic.ps.ion_classcode.domain.input.request.RequestInput
import com.isel.leic.ps.ion_classcode.domain.requests.ArchieveRepo
import com.isel.leic.ps.ion_classcode.domain.requests.Request

interface RequestRepository {
    fun createRequest(request: RequestInput): Int
    fun changeStatusRequest(id: Int,status:String)
    fun getRequests(): List<Request>
    fun getRequestById(id: Int): Request
    fun getRequestsByUser(userId: Int): List<Request>

}
