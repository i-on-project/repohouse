package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.input.AssigmentInput
import com.isel.leic.ps.ion_classcode.domain.input.FeedbackInput
import com.isel.leic.ps.ion_classcode.domain.input.request.CompositeInput
import com.isel.leic.ps.ion_classcode.domain.input.request.ArchieveRepoInput
import com.isel.leic.ps.ion_classcode.domain.input.request.RequestInput
import com.isel.leic.ps.ion_classcode.domain.requests.Composite
import com.isel.leic.ps.ion_classcode.domain.requests.ArchieveRepo
import com.isel.leic.ps.ion_classcode.domain.requests.Request

interface CompositeRepository {
    fun createCompositeRequest(request: CompositeInput): Int
    fun changeStatusCompositeRequest(id: Int,status:String)
    fun getCompositeRequests(): List<Composite>
    fun getCompositeRequestById(id: Int): Composite
    fun getCompositeRequestsByUser(userId: Int): List<Composite>

}
