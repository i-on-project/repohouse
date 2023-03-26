package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.input.AssigmentInput
import com.isel.leic.ps.ion_classcode.domain.input.FeedbackInput
import com.isel.leic.ps.ion_classcode.domain.input.request.ApplyInput
import com.isel.leic.ps.ion_classcode.domain.input.request.ArchieveRepoInput
import com.isel.leic.ps.ion_classcode.domain.input.request.RequestInput
import com.isel.leic.ps.ion_classcode.domain.requests.Apply
import com.isel.leic.ps.ion_classcode.domain.requests.ArchieveRepo
import com.isel.leic.ps.ion_classcode.domain.requests.Request

interface ApplyRequestRepository {
    fun createApplyRequest(request: ApplyInput): Int
    fun getApplyRequests(): List<Apply>
    fun getApplyRequestById(id: Int): Apply
    fun getApplyRequestsByUser(teacherId: Int): List<Apply>

}
