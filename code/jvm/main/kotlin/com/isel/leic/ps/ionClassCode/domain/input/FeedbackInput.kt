package com.isel.leic.ps.ionClassCode.domain.input

/**
 * Feedback Input Interface
 */
data class FeedbackInput(
    val description: String,
    val label: String,
    val teamId: Int,
) {
    fun isNotValid() = description.isBlank() || label.isBlank()
}
