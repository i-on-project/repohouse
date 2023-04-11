package com.isel.leic.ps.ion_classcode.domain.input

/**
 * Feedback Input Interface
 */
data class FeedbackInput(
    val description: String,
    val label: String,
    val teamId: Int,
) {
    init {
        require(description.isNotBlank()) { "Feedback description cannot be blank" }
        require(label.isNotBlank()) { "Feedback label cannot be blank" }
        require(teamId > 0) { "Team id must be greater than 0" }
    }
}
