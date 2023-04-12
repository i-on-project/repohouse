package com.isel.leic.ps.ion_classcode.domain.input

/**
 * Repository Input Interface
 */
data class RepoInput(
    val name: String,
    val url: String? = null,
    val teamId: Int
) {
    init {
        require(name.isNotBlank()) { "Repo name cannot be blank" }
        require(teamId > 0) { "Team id must be greater than 0" }
    }
}
