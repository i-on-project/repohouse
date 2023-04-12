package com.isel.leic.ps.ion_classcode.domain.input

data class FeedbackInput(
    val description: String,
    val label: String,
    val teamId: Int,
) {
    fun isNotValid() = description.isBlank() || label.isBlank() || teamId <= 0
}
