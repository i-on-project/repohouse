package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.input.ApplyInput
import com.isel.leic.ps.ion_classcode.domain.Apply
import com.isel.leic.ps.ion_classcode.domain.PendingTeacher

/**
 * Repository functions for the Apply Request Model
 */
interface ApplyRepository {
    fun createApplyRequest(request: ApplyInput): Int
    fun getApplyRequests(): List<Apply>
    fun getApplyRequestById(id: Int): Apply?
    fun getApplyRequestsByUser(teacherId: Int): List<Apply>
    fun getPendingTeacherByApply(applyId: Int): PendingTeacher?
    fun changeApplyRequestState(id: Int, state: String): Int
}
