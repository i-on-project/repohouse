package com.isel.leic.ps.ion_classcode.repository.request

import com.isel.leic.ps.ion_classcode.domain.input.request.*
import com.isel.leic.ps.ion_classcode.domain.requests.*

interface LeaveCourseRepository {
    fun createLeaveCourseRequest(request: LeaveCourseInput): Int
    fun getLeaveCourseRequests(): List<LeaveCourse>
    fun getLeaveCourseRequestById(id: Int): LeaveCourse
    fun getLeaveCourseRequestsByUser(userId: Int): List<LeaveCourse>
}
