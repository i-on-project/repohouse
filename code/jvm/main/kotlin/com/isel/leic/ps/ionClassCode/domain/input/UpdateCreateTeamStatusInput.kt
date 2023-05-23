package com.isel.leic.ps.ionClassCode.domain.input

interface UpdateRequest {
    val requestId: Int
}

data class UpdateCompositeState(
    override val requestId: Int,
) : UpdateRequest

data class UpdateCreateTeamRequestState(
    override val requestId: Int,
    val state: String,
    val gitHubTeamId: Int?
) : UpdateRequest

data class UpdateCreateRepoState(
    override val requestId: Int,
    val state: String,
    val url: String?,
    val repoId: Int,
) : UpdateRequest

data class UpdateJoinTeamState(
    override val requestId: Int,
    val state: String,
    val userId: Int,
) : UpdateRequest

data class UpdateCreateTeamStatusInput(
    val composite: UpdateCompositeState,
    val createTeam: UpdateCreateTeamRequestState,
    val joinTeam: UpdateJoinTeamState,
    val createRepo: UpdateCreateRepoState,
)
