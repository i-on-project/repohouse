package com.isel.leic.ps.ionClassCode.domain.requests

/**
 * Create Repo Request Interface
 */
data class CreateRepo(
    override val id: Int,
    override val creator: Int,
    override val state: String = "Pending",
    override val composite: Int,
    val repoId: Int,
    val repoName: String,
) : RequestInterface
