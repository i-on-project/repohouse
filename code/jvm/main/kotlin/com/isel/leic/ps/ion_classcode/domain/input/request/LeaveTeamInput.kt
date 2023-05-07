package com.isel.leic.ps.ion_classcode.domain.input.request

/**
 * Leave Team Request Input Interface
 */
data class LeaveTeamInput(
    val teamId: Int,
    override val composite: Int? = null,
) : RequestInputInterface {
    fun isNotValid(): Boolean {
        return composite != null && composite <= 0
    }
}
