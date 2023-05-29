package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveCourseInput
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveCourse

/**
 * Repository functions for Leave Course Repository
 */
interface LeaveCourseRepository {
    fun createLeaveCourseRequest(request: LeaveCourseInput, creator: Int): LeaveCourse
    fun getLeaveCourseRequests(): List<LeaveCourse>
    fun getLeaveCourseRequestById(id: Int): LeaveCourse?
    fun getLeaveCourseRequestsByUser(userId: Int): List<LeaveCourse>
    fun getLeaveCourseRequestsByCourse(courseId: Int): List<LeaveCourse>
}
