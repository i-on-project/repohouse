package com.isel.leic.ps.ion_classcode.domain.input.request

/**
 * Leave Course Request Input Interface
 */
data class LeaveCourseInput(
    val courseId: Int,
    override val composite: Int? = null,
) : RequestInputInterface {
    init {
        require(courseId > 0) { "Course id must be greater than 0" }
    }
}
