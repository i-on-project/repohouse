package com.isel.leic.ps.ion_classcode.domain

/**
 * Feedback Domain Interface
 */
data class Feedback(
    val id: Int,
    val description: String,
    val label: String,
    val teamId: Int,
) {
    init {
        require(id > 0) { "Feedback id must be greater than 0" }
        require(description.isNotBlank()) { "Feedback description cannot be blank" }
        require(label.isNotBlank()) { "Feedback label cannot be blank" }
        require(teamId > 0) { "Team id must be greater than 0" }
    }
}
