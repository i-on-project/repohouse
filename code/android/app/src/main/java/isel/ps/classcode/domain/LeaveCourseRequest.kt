package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeLeaveCourseRequestDeserialization

data class LeaveCourseRequest(
    val leaveCourse: LeaveCourse,
    val leaveTeamRequests: List<LeaveTeam>,
) {
    constructor(deserialization: ClassCodeLeaveCourseRequestDeserialization) : this(
        leaveCourse = LeaveCourse(deserialization = deserialization.leaveCourse),
        leaveTeamRequests = deserialization.leaveTeamRequests.map { LeaveTeam(deserialization = it) },
    )
}
