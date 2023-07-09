package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeLeaveClassroomDeserialization

/**
 * Represents a ClassCode Leave Classroom
 */
data class LeaveClassroom(
    val requestId: Int,
    val creator: Int,
    val state: String = "Pending",
    val classroomId: Int,
    val composite: Int,
    val githubUsername: String,
) {
    constructor(deserialization: ClassCodeLeaveClassroomDeserialization) : this (
        requestId = deserialization.id,
        creator = deserialization.creator,
        state = deserialization.state,
        classroomId = deserialization.classroomId,
        composite = deserialization.composite,
        githubUsername = deserialization.githubUsername,
    )
}
