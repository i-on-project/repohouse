package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeLeaveClassroomRequestDeserialization

/**
 * Represents a ClassCode Leave Classroom Request
 */

data class LeaveClassroomRequest(
    val leaveClassroom: LeaveClassroom,
    val leaveTeamRequests: List<LeaveTeamWithRepoName>,
) {
    constructor(deserialization: ClassCodeLeaveClassroomRequestDeserialization) : this(
        leaveClassroom = LeaveClassroom(deserialization = deserialization.leaveClassroom),
        leaveTeamRequests = deserialization.leaveTeamRequests.map { LeaveTeamWithRepoName(deserialization = it) },
    )
}
