package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveClassroomInput
import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveCourseInput
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveClassroom
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveCourse

/**
 * Repository functions for Leave Classroom Repository
 */
interface LeaveClassroomRepository {
    fun createLeaveClassroomRequest(request: LeaveClassroomInput, creator: Int): LeaveClassroom
    fun getLeaveClassroomRequests(): List<LeaveClassroom>
    fun getLeaveClassroomRequestById(id: Int): LeaveClassroom?
    fun getLeaveClassroomRequestsByUser(userId: Int): List<LeaveClassroom>
    fun getLeaveClassroomRequestsByCourse(classroomId: Int): List<LeaveClassroom>
}
