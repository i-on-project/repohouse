package com.isel.leic.ps.ionClassCode.domain

/**
 * Team Domain Interface
 */
data class Team(
    val id: Int,
    val name: String,
    val isCreated: Boolean,
    val assignment: Int
)

data class TeamNotCreated(
    val teamId: Int,
    val name: String,
    val id: Int,
    val creator: Int,
    val state: String = "Pending",
    val composite: Int,
    val githubTeamId: Int?,
)

data class UserJoinTeam(
    val githubUsername: String,
    val id: Int,
    val creator: Int,
    val state: String = "Pending",
    val composite: Int,
)
