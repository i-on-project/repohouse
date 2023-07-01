package isel.ps.classcode.domain

/**
 * Represents a team
 */

data class Teams(
    val teamsCreated: List<Team>,
    val createTeamComposite: List<CreateTeamComposite>,
)
