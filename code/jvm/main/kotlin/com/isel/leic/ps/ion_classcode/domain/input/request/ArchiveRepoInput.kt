package com.isel.leic.ps.ion_classcode.domain.input.request

data class ArchiveRepoInput(
    val repoId: Int,
    override val composite: Int? = null,
    override val creator: Int
) : RequestInput {
    init {
        require(repoId > 0) { "Repo id must be greater than 0" }
    }
}
