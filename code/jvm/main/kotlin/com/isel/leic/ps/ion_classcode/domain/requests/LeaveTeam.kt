package com.isel.leic.ps.ion_classcode.domain.requests

data class LeaveTeam(
    override val id: Int,
    override val creator: Int,
    override val state: String = "pending",
    val teamId: Int,
    val compositeId: Int? = null
) : RequestInterface {
    init {
        require(checkState()) { "Invalid state" }
    }
}
