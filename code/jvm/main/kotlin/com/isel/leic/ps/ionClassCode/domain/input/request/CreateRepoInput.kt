package com.isel.leic.ps.ionClassCode.domain.input.request

/**
 * Create Repo Request Input Interface
 */
data class CreateRepoInput(
    val repoId: Int,
    override val composite: Int,
) : RequestInputInterface
