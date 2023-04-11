package com.isel.leic.ps.ion_classcode.domain.input.request

/**
 * Create Team Request Input Interface
 */
data class CreateTeamInput(
    override val composite: Int? = null,
    override val creator: Int
) : RequestInputInterface {
    init {
        require(creator > 0) { "Creator must be greater than 0" }
    }
}
