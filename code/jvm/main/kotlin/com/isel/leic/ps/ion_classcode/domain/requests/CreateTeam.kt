package com.isel.leic.ps.ion_classcode.domain.requests

/**
 * Create Team Request Interface
 */
data class CreateTeam(
    override val id: Int,
    override val creator: Int,
    override val state: String = "Pending",
    override val composite: Int? = null
) : RequestInterface
