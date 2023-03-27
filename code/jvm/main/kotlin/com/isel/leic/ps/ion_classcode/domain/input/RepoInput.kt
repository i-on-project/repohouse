package com.isel.leic.ps.ion_classcode.domain.input

data class RepoInput(
    val name: String,
    val url: String,
    val team_id: Int
) {
    init {
        require(name.isNotBlank()) { "Repo name cannot be blank" }
        require(url.isNotBlank()) { "Repo url cannot be blank" }
        require(team_id > 0) { "Team id must be greater than 0" }
    }
}
