package com.isel.leic.ps.ion_classcode.domain.input.request

data class CreateRepoInputInterface(
    override val composite: Int? = null,
    override val creator: Int,
    val repoId: Int
) : RequestInputInterface
