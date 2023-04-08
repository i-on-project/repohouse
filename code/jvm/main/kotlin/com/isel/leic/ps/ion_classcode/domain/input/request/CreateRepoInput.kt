package com.isel.leic.ps.ion_classcode.domain.input.request

data class CreateRepoInput(
    val teamId:Int,
    override val composite: Int? = null,
    override val creator: Int,
) : RequestInputInterface
