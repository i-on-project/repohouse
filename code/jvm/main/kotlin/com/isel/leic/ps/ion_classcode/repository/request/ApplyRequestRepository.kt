package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.ApplyInput
import com.isel.leic.ps.ion_classcode.domain.requests.Apply

/**
 * Repository functions for the Apply Request Model
 */
interface ApplyRequestRepository {
    fun createApplyRequest(request: ApplyInput,creator:Int): Int
    fun getApplyRequests(): List<Apply>
    fun getApplyRequestById(id: Int): Apply?
    fun getApplyRequestsByUser(teacherId: Int): List<Apply>
}
