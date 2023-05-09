package com.isel.leic.ps.ionClassCode.domain.requests

/**
 * Join Team Request Interface
 */
data class JoinTeam(
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
