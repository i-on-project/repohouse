package com.isel.leic.ps.ion_classcode.domain.input.request

/**
 * Create Repo Request Input Interface
 */
data class CreateRepoInput(
    val teamId: Int,
    override val composite: Int? = null,
) : RequestInputInterface
