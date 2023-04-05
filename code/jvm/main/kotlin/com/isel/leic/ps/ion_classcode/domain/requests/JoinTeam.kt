package com.isel.leic.ps.ion_classcode.domain.requests

data class JoinTeam(
    override val id: Int,
    override val creator: Int,
    override val state: String = "Pending",
    val teamId: Int,
    override val compositeId: Int? = null
) : RequestInterface {
    init {
        require(checkState()) { "Invalid state" }
    }
}
