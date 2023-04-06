package com.isel.leic.ps.ion_classcode.domain.requests

data class CreateTeam(
    override val id: Int,
    override val creator: Int,
    override val state: String = "pending",
    override val composite: Int? = null
) : RequestInterface {
    init {
        require(checkState()) { "Invalid state" }
    }
}
