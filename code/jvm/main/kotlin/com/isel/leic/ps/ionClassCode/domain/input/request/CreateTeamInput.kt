package com.isel.leic.ps.ionClassCode.domain.input.request

data class CreateTeamInput(
    val teamId: Int,
    val teamName: String,
    override val composite: Int,
) : RequestInputInterface
