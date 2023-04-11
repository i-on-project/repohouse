package com.isel.leic.ps.ion_classcode.domain.requests

/**
 * Leave Team Request Interface
 */
data class LeaveTeam(
    override val id: Int,
    override val creator: Int,
    override val state: String = "Pending",
    val teamId: Int,
    override val composite: Int? = null
) : RequestInterface {
    init {
        require(checkState()) { "Invalid state" }
    }
}
