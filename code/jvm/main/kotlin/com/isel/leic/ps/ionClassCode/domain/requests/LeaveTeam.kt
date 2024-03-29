package com.isel.leic.ps.ionClassCode.domain.requests

/**
 * Leave Team Request Interface
 */
data class LeaveTeam(
    override val id: Int,
    override val creator: Int,
    override val state: String = "Pending",
    override val composite: Int? = null,
    val teamId: Int,
    val githubUsername: String,
    val membersCount: Int,
    val teamName: String,
) : RequestInterface {
    init {
        require(checkState()) { "Invalid state" }
    }
}

/**
 * Leave Team Request with Repo Name
 */
data class LeaveTeamWithRepoName(
    val leaveTeam: LeaveTeam,
    val repoName: String,
)
