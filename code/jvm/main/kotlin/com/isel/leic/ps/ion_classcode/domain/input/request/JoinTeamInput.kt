package com.isel.leic.ps.ion_classcode.domain.input.request

/**
 * Join Team Request Input Interface
 */
data class JoinTeamInput(
    val assignmentId: Int,
    val teamId: Int,
    override val composite: Int? = null,
    override val creator: Int
) : RequestInputInterface {
    fun isNotValid() = teamId <= 0 || creator <= 0 || assignmentId <= 0
}
