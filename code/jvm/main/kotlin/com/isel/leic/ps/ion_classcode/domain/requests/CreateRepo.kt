package com.isel.leic.ps.ion_classcode.domain.requests

data class CreateRepo(
    override val id: Int,
    override val creator: Int,
    override val state: String = "pending",
    val repoId: Int,
    override val compositeId: Int? = null
) : RequestInterface {
    init {
        require(checkState()) { "Invalid state" }
    }
}
