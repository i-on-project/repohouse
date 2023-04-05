package com.isel.leic.ps.ion_classcode.domain.input.request

data class LeaveCourseInputInterface(
    val courseId: Int,
    override val composite: Int? = null,
    override val creator: Int
) : RequestInputInterface {
    init {
        require(courseId > 0) { "Course id must be greater than 0" }
    }
}
