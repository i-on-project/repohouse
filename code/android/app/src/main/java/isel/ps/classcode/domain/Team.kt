package isel.ps.classcode.domain

/**
 * Represents a team
 */
data class Team (
    val id: Int,
    val name: String,
    val isCreated: Boolean,
    val assignment: Int
)
