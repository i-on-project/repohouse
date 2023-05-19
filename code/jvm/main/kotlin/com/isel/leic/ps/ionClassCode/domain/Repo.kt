package com.isel.leic.ps.ionClassCode.domain

/**
 * Repository Domain Interface
 */
data class Repo(
    val id: Int,
    val url: String?,
    val name: String,
    val isCreated: Boolean,
)

data class RepoNotCreated(
    val repoId: Int,
    val name: String,
    val id: Int,
    val creator: Int,
    val state: String = "Pending",
    val composite: Int,
)
