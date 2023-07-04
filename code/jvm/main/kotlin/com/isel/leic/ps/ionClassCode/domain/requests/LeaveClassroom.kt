package com.isel.leic.ps.ionClassCode.domain.requests

/**
 * Leave Classroom Request Interface
 */
data class LeaveClassroom(
    override val id: Int,
    override val creator: Int,
    override val state: String = "Pending",
    val classroomId: Int,
    override val composite: Int,
    val githubUsername: String,
) : RequestInterface {
    init {
        require(checkState()) { "Invalid state" }
    }
}
