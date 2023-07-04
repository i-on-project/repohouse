package com.isel.leic.ps.ionClassCode.domain.input.request

/**
 * Leave Course Request Input Interface
 */
data class LeaveClassroomInput(
    val classroomId: Int,
    override val composite: Int,
    val githubUsername: String,
) : RequestInputInterface {
    init {
        require(classroomId > 0) { "Course id must be greater than 0" }
    }
}
