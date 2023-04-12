package com.isel.leic.ps.ion_classcode.domain.input.request

/**
 * Leave Team Request Input Interface
 */
data class LeaveTeamInput(
    val teamId: Int,
    override val composite: Int? = null,
    override val creator: Int,
) : RequestInputInterface {
    fun isNotValid() = teamId <= 0 || creator <= 0
}
