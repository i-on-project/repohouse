package com.isel.leic.ps.ion_classcode.domain.input.request

data class LeaveCourseInput(
    val courseId:Int,
    override val composite:Int? = null,
    override val creator: Int
) :RequestInput{
    init {
        require(courseId > 0) { "Course id must be greater than 0" }
    }
}


