package com.isel.leic.ps.ionClassCode.domain.input

interface UpdateRequest {
    val requestId: Int
    val state: String
}

data class UpdateRequestState(
    override val requestId: Int,
    override val state: String,
) : UpdateRequest

data class UpdateCreateRepoState(
    override val requestId: Int,
    override val state: String,
    val url: String,
    val repoId: Int,
) : UpdateRequest

data class UpdateJoinTeamState(
    override val requestId: Int,
    override val state: String,
    val userId: Int,
) : UpdateRequest

data class UpdateCreateTeamStatusInput(
    val composite: UpdateRequestState,
    val createTeam: UpdateRequestState,
    val joinTeam: UpdateJoinTeamState,
    val createRepo: UpdateCreateRepoState,
)
