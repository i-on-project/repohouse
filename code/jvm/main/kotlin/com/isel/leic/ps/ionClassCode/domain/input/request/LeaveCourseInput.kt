package com.isel.leic.ps.ionClassCode.domain.input.request

/**
 * Leave Course Request Input Interface
 */
data class LeaveCourseInput(
    val courseId: Int,
    override val composite: Int,
    val githubUsername: String,
) : RequestInputInterface {
    init {
        require(courseId > 0) { "Course id must be greater than 0" }
    }
}
