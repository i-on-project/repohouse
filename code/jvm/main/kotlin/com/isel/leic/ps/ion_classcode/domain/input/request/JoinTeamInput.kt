package com.isel.leic.ps.ion_classcode.domain.input.request

/**
 * Join Team Request Input Interface
 */
data class JoinTeamInput(
    val assigmentId:Int,
    val teamId: Int,
    override val composite: Int? = null,
    override val creator: Int
) : RequestInputInterface {
    init {
        require(creator > 0) { "Creator must be greater than 0" }
        require(teamId > 0) { "TeamId must be greater than 0" }
    }
}
