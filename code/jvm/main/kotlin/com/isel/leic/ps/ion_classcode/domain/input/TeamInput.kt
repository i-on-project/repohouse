package com.isel.leic.ps.ion_classcode.domain.input

/**
 * Team Input Interface
 */
data class TeamInput(
    val name: String,
    val assignmentId: Int,
    val isCreated: Boolean,
) {
    init {
        require(name.isNotBlank()) { "Team name cannot be blank" }
        require(assignmentId > 0) { "Assigment id must be greater than 0" }
    }
}
