package com.isel.leic.ps.ionClassCode.domain.input.request

/**
 * Join Team Request Input Interface
 */
data class JoinTeamInput(
    val assignmentId: Int,
    val teamId: Int,
    override val composite: Int? = null,
) : RequestInputInterface {
    fun isNotValid(): Boolean{
        return composite != null && composite <= 0
    }
}
