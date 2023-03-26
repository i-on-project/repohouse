package com.isel.leic.ps.ion_classcode.domain.input.request

data class CreateTeamInput(
    val teamId:Int,
    override val composite:Int? = null,
    override val creator: Int
):RequestInput {
    init {
        require(teamId > 0) { "Team id must be greater than 0" }
    }
}


