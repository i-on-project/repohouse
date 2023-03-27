package com.isel.leic.ps.ion_classcode.domain.requests

data class LeaveCourse(
    override val id: Int,
    override val creator: Int,
    override val state: String = "pending",
    val courseId: Int,
    val compositeId: Int? = null
) : Request {
    init {
        require(checkState()) { "Invalid state" }
    }
}
