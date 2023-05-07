package com.isel.leic.ps.ion_classcode.domain.requests

/**
 * Create Repo Request Interface
 */
data class CreateRepo(
    override val id: Int,
    val teamId: Int,
    override val creator: Int,
    override val state: String = "Pending",
    override val composite: Int? = null
) : RequestInterface
