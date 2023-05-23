package com.isel.leic.ps.ionClassCode.domain.input

/**
 * Update Request Input Interface
 */
interface UpdateRequest {
    val requestId: Int
}

/**
 * Update Composite State Input
 */

data class UpdateCompositeState(
    override val requestId: Int,
) : UpdateRequest

/**
 * Update Create Team State Input
 */

data class UpdateCreateTeamRequestState(
    override val requestId: Int,
    val state: String,
    val gitHubTeamId: Int?
) : UpdateRequest

/**
 * Update Create Repo Request State Input Interface
 */
data class UpdateCreateRepoState(
    override val requestId: Int,
    val state: String,
    val url: String?,
    val repoId: Int,
) : UpdateRequest

/**
 * Update Join Team Request State Input Interface
 */
data class UpdateJoinTeamState(
    override val requestId: Int,
    val state: String,
    val userId: Int,
) : UpdateRequest

/**
 * Update Create Team Status Input Interface
 */
data class UpdateCreateTeamStatusInput(
    val composite: UpdateCompositeState,
    val createTeam: UpdateCreateTeamRequestState,
    val joinTeam: UpdateJoinTeamState,
    val createRepo: UpdateCreateRepoState,
)
