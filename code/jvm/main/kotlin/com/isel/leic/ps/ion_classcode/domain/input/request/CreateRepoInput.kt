package com.isel.leic.ps.ion_classcode.domain.input.request

data class CreateRepoInput(
    override val composite: Int? = null,
    override val creator: Int,
) : RequestInputInterface
