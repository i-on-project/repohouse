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
    val gitHubTeamId: Int?,
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
data class UpdateArchiveRepoState(
    override val requestId: Int,
    val state: String,
) : UpdateRequest

data class UpdateArchiveRepoInput(
    val composite: UpdateCompositeState,
    val archiveRepos: List<UpdateArchiveRepoState>,
)

data class LeaveTeamWithDelete(override val requestId: Int, val state: String = "Pending", val teamId: Int, val wasDeleted: Boolean = false) : UpdateRequest

data class UpdateLeaveCourse(
    override val requestId: Int,
    val courseId: Int,
) : UpdateRequest

data class UpdateLeaveClassroom(
    override val requestId: Int,
    val classroomId: Int,
) : UpdateRequest

data class UpdateLeaveCourseCompositeInput(
    val composite: UpdateCompositeState,
    val leaveCourse: UpdateLeaveCourse,
    val leaveClassrooms: List<UpdateLeaveClassroomCompositeInput>
)

data class UpdateLeaveClassroomCompositeInput(
    val composite: UpdateCompositeState,
    val leaveClassroom: UpdateLeaveClassroom,
    val leaveTeams: List<LeaveTeamWithDelete>
)
