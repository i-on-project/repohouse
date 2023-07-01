package com.isel.leic.ps.ionClassCode.domain.requests

/**
 * Leave Course Request Interface
 */
data class LeaveCourse(
    override val id: Int,
    override val creator: Int,
    override val state: String = "Pending",
    val courseId: Int,
    override val composite: Int,
    val githubUsername: String,
) : RequestInterface {
    init {
        require(checkState()) { "Invalid state" }
    }
}
