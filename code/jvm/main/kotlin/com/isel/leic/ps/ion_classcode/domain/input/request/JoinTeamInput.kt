package com.isel.leic.ps.ion_classcode.domain.input.request

/**
 * Join Team Request Input Interface
 */
data class JoinTeamInput(
    val assignmentId: Int,
    val teamId: Int,
    override val composite: Int? = null,
) : RequestInputInterface {
    fun isNotValid(): Boolean{
        val cond = teamId <= 0 || assignmentId <= 0
        return if (composite == null) {
            cond
        } else {
            cond || composite <= 0
        }
    }
}
