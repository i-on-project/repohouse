package com.isel.leic.ps.ionClassCode.http.model.output

import com.isel.leic.ps.ionClassCode.domain.Assignment
import com.isel.leic.ps.ionClassCode.domain.Delivery
import com.isel.leic.ps.ionClassCode.domain.RepoNotCreated
import com.isel.leic.ps.ionClassCode.domain.Team
import com.isel.leic.ps.ionClassCode.domain.TeamNotCreated
import com.isel.leic.ps.ionClassCode.domain.UserJoinTeam

/**
 * Represents a Assigment Output Model.
 */
interface AssigmentOutputModel {
    val assignment: Assignment
    val deliveries: List<Delivery>
}

/**
 * Represents a Teacher Assigment Output Model.
 */
data class TeacherAssignmentOutputModel(
    override val assignment: Assignment,
    override val deliveries: List<Delivery>,
    val teams: List<Team>,
) : AssigmentOutputModel

/**
 * Represents a Student Assigment Output Model.
 */
data class StudentAssignmentOutputModel(
    override val assignment: Assignment,
    override val deliveries: List<Delivery>,
    val team: Team?,
) : AssigmentOutputModel

/**
 * Represents a Assigment Created Output Model.
 */
data class AssignmentCreatedOutputModel(
    val assignment: Assignment,
)

/**
 * Represents a Assigment Created Output Model.
 */
data class AssignmentDeletedOutputModel(
    val deleted: Boolean,
)

/**
 * Represents a Assigment Model for inner functions.
 */
sealed class AssignmentModel {
    abstract val assignment: Assignment
    abstract val deliveries: List<Delivery>
}

/**
 * Represents an Assigment Model for the teacher.
 */
data class TeacherAssignmentModel(
    override val assignment: Assignment,
    override val deliveries: List<Delivery>,
    val teams: List<Team>,
) : AssignmentModel()

/**
 * Represents a Create Team Composite.
 */
data class CreateTeamComposite(
    val compositeState: String,
    val createTeam: TeamNotCreated,
    val joinTeam: UserJoinTeam,
    val createRepo: RepoNotCreated,
)

/**
 * Represents an Assigment Model for the teacher assignment teams.
 */
data class TeacherAssignmentTeams(
    val assignment: Assignment,
    val teamsCreated: List<Team>,
    val createTeamComposites: List<CreateTeamComposite>,
)

/**
 * Represents an Assigment Model for the student.
 */
data class StudentAssignmentModel(
    override val assignment: Assignment,
    override val deliveries: List<Delivery>,
    val team: Team?,
) : AssignmentModel()
