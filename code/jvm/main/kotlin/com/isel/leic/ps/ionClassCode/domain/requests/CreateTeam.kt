package com.isel.leic.ps.ionClassCode.domain.requests

/**
 * Create Team Request Interface
 */
data class CreateTeam(
    override val id: Int,
    override val creator: Int,
    override val state: String = "Pending",
    override val composite: Int,
    val teamId: Int,
    val githubTeamId: Int?
) : RequestInterface
