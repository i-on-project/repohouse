package com.isel.leic.ps.ionClassCode.repository

import com.isel.leic.ps.ionClassCode.domain.Apply
import com.isel.leic.ps.ionClassCode.domain.PendingTeacher
import com.isel.leic.ps.ionClassCode.domain.input.ApplyInput

/**
 * Repository functions for the Apply Request Model
 */
interface ApplyRepository {
    fun createApplyRequest(request: ApplyInput): Apply
    fun getApplyRequests(): List<Apply>
    fun getApplyRequestById(id: Int): Apply?
    fun getApplyRequestsByUser(teacherId: Int): List<Apply>
    fun getPendingTeacherByApply(applyId: Int): PendingTeacher?
    fun changeApplyRequestState(id: Int, state: String): Boolean
}
