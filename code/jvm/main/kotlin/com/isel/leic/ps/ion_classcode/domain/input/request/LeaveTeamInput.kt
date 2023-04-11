package com.isel.leic.ps.ion_classcode.domain.input.request

/**
 * Leave Team Request Input Interface
 */
data class LeaveTeamInput(
    val teamId: Int,
    override val composite: Int? = null,
    override val creator: Int
) : RequestInputInterface {
    init {
        require(teamId > 0) { "Team id must be greater than 0" }
        require(creator > 0) { "Creator id must be greater than 0" }
    }
}
