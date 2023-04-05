package com.isel.leic.ps.ion_classcode.domain.requests

data class LeaveCourse(
    override val id: Int,
    override val creator: Int,
    override val state: String = "Pending",
    val courseId: Int,
    override val composite: Int? = null
) : RequestInterface {
    init {
        require(checkState()) { "Invalid state" }
    }
}
