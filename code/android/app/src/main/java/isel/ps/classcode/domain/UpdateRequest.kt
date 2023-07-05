package isel.ps.classcode.domain

/**
 * Represents a UpdateRequest
 */

interface UpdateRequest {
    val requestId: Int
}

/**
 * Represents a UpdateCompositeState
 */

data class UpdateCompositeState(
    override val requestId: Int,
) : UpdateRequest

/**
 * Represents a UpdateCreateTeamRequestState
 */

data class UpdateCreateTeamRequestState(
    override val requestId: Int,
    val state: String,
    val gitHubTeamId: Int?,
) : UpdateRequest

/**
 * Represents a UpdateCreateRepoState
 */

data class UpdateCreateRepoState(
    override val requestId: Int,
    val state: String,
    val url: String?,
    val repoId: Int,
) : UpdateRequest

/**
 * Represents a UpdateJoinTeamState
 */

data class UpdateJoinTeamState(
    override val requestId: Int,
    val state: String,
    val userId: Int,
) : UpdateRequest

/**
 * Represents a UpdateCreateTeamStatusInput
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

data class UpdateRequestStateInput(val type: String, val creator: Int, val requestId: Int, val state: String) {
    constructor(isJoinTeam: Boolean, creator: Int, requestId: Int, state: String) : this(
        type = if (isJoinTeam) "jointeam" else "leaveteam",
        creator = creator,
        requestId = requestId,
        state = state,
    )
}

/**
 * Change Request State Input
 */
data class LeaveRequestStateInput(override val requestId: Int) : UpdateRequest

data class LeaveTeamWithDelete(override val requestId: Int, val state: String = "Pending", val teamId: Int, val wasDeleted: Boolean = false) : UpdateRequest

data class UpdateLeaveCourse(
    override val requestId: Int,
    val courseId: Int,
) : UpdateRequest

data class UpdateLeaveClassroom(
    override val requestId: Int,
    val classroomId: Int,
) : UpdateRequest

data class UpdateLeaveClassroomCompositeInput(
    val composite: UpdateCompositeState,
    val leaveClassroom: UpdateLeaveClassroom,
    val leaveTeams: List<LeaveTeamWithDelete>
)
data class UpdateLeaveCourseCompositeInput(
    val composite: UpdateCompositeState,
    val leaveCourse: UpdateLeaveCourse,
    val leaveClassrooms: List<UpdateLeaveClassroomCompositeInput>,
)
