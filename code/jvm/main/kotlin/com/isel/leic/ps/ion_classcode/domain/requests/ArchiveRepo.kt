package com.isel.leic.ps.ion_classcode.domain.requests

data class ArchiveRepo(
    override val id: Int,
    override val creator: Int,
    override val state: String = "Pending",
    override val composite: Int? = null,
    val repoId: Int,
) : RequestInterface {
    init {
        require(checkState()) { "Invalid state" }
    }
}
