package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeLeaveCourseRequestDeserialization

/**
 * Represents a ClassCode Leave Course Request
 */
data class LeaveCourseRequest(
    val leaveCourse: LeaveCourse,
    val leaveClassroomRequests: List<LeaveClassroomRequest>,
) {
    constructor(deserialization: ClassCodeLeaveCourseRequestDeserialization) : this(
        leaveCourse = LeaveCourse(deserialization = deserialization.leaveCourse),
        leaveClassroomRequests = deserialization.leaveClassroomRequests.map { LeaveClassroomRequest(deserialization = it) },
    )
}
