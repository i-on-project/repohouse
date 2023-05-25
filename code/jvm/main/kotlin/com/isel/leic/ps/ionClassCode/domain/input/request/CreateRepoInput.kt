package com.isel.leic.ps.ionClassCode.domain.input.request

/**
 * Create Repo Request Input Interface
 */
data class CreateRepoInput(
    val repoId: Int,
    val repoName: String,
    override val composite: Int,
) : RequestInputInterface
