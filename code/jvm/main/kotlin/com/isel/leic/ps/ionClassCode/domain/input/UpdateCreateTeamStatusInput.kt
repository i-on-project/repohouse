package com.isel.leic.ps.ionClassCode.domain.input

/**
 * Update Request Input Interface
 */
interface UpdateRequest {
    val requestId: Int
    val state: String
}

/**
 * Update Request State Input Interface
 */
data class UpdateRequestState(
    override val requestId: Int,
    override val state: String,
) : UpdateRequest

/**
 * Update Create Repo Request State Input Interface
 */
data class UpdateCreateRepoState(
    override val requestId: Int,
    override val state: String,
    val url: String,
    val repoId: Int,
) : UpdateRequest

/**
 * Update Join Team Request State Input Interface
 */
data class UpdateJoinTeamState(
    override val requestId: Int,
    override val state: String,
    val userId: Int,
) : UpdateRequest

/**
 * Update Create Team Status Input Interface
 */
data class UpdateCreateTeamStatusInput(
    val composite: UpdateRequestState,
    val createTeam: UpdateRequestState,
    val joinTeam: UpdateJoinTeamState,
    val createRepo: UpdateCreateRepoState,
)
