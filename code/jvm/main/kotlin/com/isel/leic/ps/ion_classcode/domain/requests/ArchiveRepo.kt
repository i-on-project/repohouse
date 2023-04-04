package com.isel.leic.ps.ion_classcode.domain.requests

data class ArchiveRepo(
    override val id: Int,
    override val creator: Int,
    override val state: String = "Pending",
    val repoId: Int,
    override val composite: Int? = null
) : Request {
    init {
        require(checkState()) { "Invalid state" }
    }
}
