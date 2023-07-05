package com.isel.leic.ps.ionClassCode.repository.request

import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveClassroomInput
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveClassroom

/**
 * Repository functions for Leave Classroom Repository
 */
interface LeaveClassroomRepository {
    fun createLeaveClassroomRequest(request: LeaveClassroomInput, creator: Int): LeaveClassroom
    fun getLeaveClassroomRequests(): List<LeaveClassroom>
    fun getLeaveClassroomRequestById(id: Int): LeaveClassroom?
    fun getLeaveClassroomRequestByCompositeId(composite: Int): List<LeaveClassroom>
    fun getLeaveClassroomRequestsByUser(userId: Int): List<LeaveClassroom>
    fun getLeaveClassroomRequestsByClassroom(classroomId: Int): List<LeaveClassroom>
}
