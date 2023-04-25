package com.isel.leic.ps.ion_classcode.domain.input.request

/**
 * Create Team Request Input Interface
 */
data class CreateTeamInput(
    override val composite: Int? = null,
) : RequestInputInterface {
    fun isNotValid(): Boolean {
        return composite != null && composite <= 0
    }
}
