package com.isel.leic.ps.ion_classcode.domain.input.request

/**
 * Archive Repo Request Input Interface
 */
data class ArchiveRepoInput(
    val repoId: Int,
    override val composite: Int? = null,
) : RequestInputInterface {
    init {
        require(repoId > 0) { "Repo id must be greater than 0" }
    }
}
