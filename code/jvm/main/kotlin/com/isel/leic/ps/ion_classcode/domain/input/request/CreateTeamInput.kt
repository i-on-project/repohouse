package com.isel.leic.ps.ion_classcode.domain.input.request

data class CreateTeamInput(
    override val composite: Int? = null,
    override val creator: Int
) : RequestInputInterface {
    fun isNotValid() = creator <= 0
}
