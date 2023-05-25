package com.isel.leic.ps.ionClassCode.services

import com.isel.leic.ps.ionClassCode.domain.Feedback
import com.isel.leic.ps.ionClassCode.domain.StudentWithoutToken
import com.isel.leic.ps.ionClassCode.domain.input.FeedbackInput
import com.isel.leic.ps.ionClassCode.domain.input.RepoInput
import com.isel.leic.ps.ionClassCode.domain.input.TeamInput
import com.isel.leic.ps.ionClassCode.domain.input.UpdateCreateTeamStatusInput
import com.isel.leic.ps.ionClassCode.domain.input.request.CompositeInput
import com.isel.leic.ps.ionClassCode.domain.input.request.CreateRepoInput
import com.isel.leic.ps.ionClassCode.domain.input.request.CreateTeamInput
import com.isel.leic.ps.ionClassCode.domain.input.request.JoinTeamInput
import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveTeamInput
import com.isel.leic.ps.ionClassCode.domain.requests.CreateTeam
import com.isel.leic.ps.ionClassCode.domain.requests.JoinTeam
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveTeam
import com.isel.leic.ps.ionClassCode.http.model.output.CreateTeamComposite
import com.isel.leic.ps.ionClassCode.http.model.output.RequestsHistory
import com.isel.leic.ps.ionClassCode.http.model.output.RequestsThatNeedApproval
import com.isel.leic.ps.ionClassCode.http.model.output.TeamModel
import com.isel.leic.ps.ionClassCode.http.model.output.TeamRequestsForMobileModel
import com.isel.leic.ps.ionClassCode.http.model.output.TeamRequestsModel
import com.isel.leic.ps.ionClassCode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ionClassCode.http.model.problem.Problem
import com.isel.leic.ps.ionClassCode.repository.transaction.TransactionManager
import com.isel.leic.ps.ionClassCode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias TeamResponse = Result<TeamServicesError, TeamModel>
typealias TeamsResponse = Result<TeamServicesError, List<TeamModel>>
typealias TeamRequestsResponse = Result<TeamServicesError, TeamRequestsModel>
typealias TeamRequestsMobileResponse = Result<TeamServicesError, TeamRequestsForMobileModel>
typealias TeamCreateRequestResponse = Result<TeamServicesError, CreateTeam>
typealias TeamLeaveRequestResponse = Result<TeamServicesError, LeaveTeam>
typealias TeamJoinRequestResponse = Result<TeamServicesError, JoinTeam>
typealias TeamUpdateRequestResponse = Result<TeamServicesError, Boolean>
typealias TeamFeedbackResponse = Result<TeamServicesError, Feedback>
typealias TeamUpdateStatusResponse = Result<TeamServicesError, Boolean>

/**
 * Error codes for the services
 */
sealed class TeamServicesError {
    object TeamNotAccepted : TeamServicesError()
    object RequestNotFound : TeamServicesError()
    object TeamNotFound : TeamServicesError()
    object RequestNotRejected : TeamServicesError()
    object ClassroomNotFound : TeamServicesError()
    object ClassroomArchived : TeamServicesError()
    object AssignmentNotFound : TeamServicesError()
    object InvalidData : TeamServicesError()
    object InternalError : TeamServicesError()
}

/**
 * Service to the team services
 */
@Component
class TeamServices(
    val transactionManager: TransactionManager,
) {

    /**
     * Method to get all the information about a team
     */
    fun getTeamInfo(teamId: Int): TeamResponse {
        return transactionManager.run {
            val team = it.teamRepository.getTeamById(teamId)
            if (team == null) {
                return@run Result.Problem(TeamServicesError.TeamNotFound)
            } else {
                val students = it.teamRepository.getStudentsFromTeam(teamId)
                val repo = it.repoRepository.getRepoByTeam(teamId)
                val feedbacks = it.feedbackRepository.getFeedbacksByTeam(teamId)
                return@run Result.Success(
                    TeamModel(
                        team,
                        students.map { student -> StudentWithoutToken(student.name, student.email, student.id, student.githubUsername, student.githubId, student.isCreated, student.schoolId) },
                        repo,
                        feedbacks,
                    ),
                )
            }
        }
    }

    /**
     * Method to get all the information about a team
     */
    fun getTeamsInfoByAssignment(assignmentId: Int): TeamsResponse {
        if (assignmentId <= 0) return Result.Problem(value = TeamServicesError.InvalidData)
        return transactionManager.run {
            val teams = it.teamRepository.getTeamsFromAssignment(assignmentId = assignmentId)
            Result.Success(
                value = teams.map { team ->
                    TeamModel(
                        team = team,
                        students = it.teamRepository.getStudentsFromTeam(teamId = team.id).map { student -> StudentWithoutToken(student.name, student.email, student.id, student.githubUsername, student.githubId, student.isCreated, student.schoolId) },
                        repo = it.repoRepository.getRepoByTeam(teamId = team.id),
                        feedbacks = it.feedbackRepository.getFeedbacksByTeam(teamId = team.id),
                    )
                },
            )
        }
    }

    /**
     * Method to create a request to create a team
     * Needs a teacher to approve the request
     */
    fun createTeamRequest(creator: Int, creatorGitHubUserName: String, assignmentId: Int, classroomId: Int): TeamCreateRequestResponse {
        return transactionManager.run {
            it.usersRepository.getStudent(creator) ?: return@run Result.Problem(value = TeamServicesError.InternalError)
            val classroom = it.classroomRepository.getClassroomById(classroomId) ?: return@run Result.Problem(value = TeamServicesError.ClassroomNotFound)
            it.assignmentRepository.getAssignmentById(assignmentId) ?: return@run Result.Problem(value = TeamServicesError.AssignmentNotFound)
            if (classroom.isArchived) return@run Result.Problem(value = TeamServicesError.ClassroomArchived)
            val team = it.teamRepository.createTeam(
                team = TeamInput(
                    "${classroom.name} - $assignmentId",
                    assignmentId,
                    false,
                ),
            )
            val repo = it.repoRepository.createRepo(repo = RepoInput(name = "${classroom.name} - $assignmentId - ${team.id}", url = null, teamId = team.id))
            val composite = it.compositeRepository.createCompositeRequest(request = CompositeInput(), creator = creator)
            val createTeam = it.createTeamRepository.createCreateTeamRequest(request = CreateTeamInput(teamId = team.id, composite = composite.id, teamName = team.name), creator = creator)
            println(team.id)
            println(composite.id)
            println(team.name)
            it.createRepoRepository.createCreateRepoRequest(request = CreateRepoInput(repoId = repo.id, composite = composite.id, repoName = repo.name), creator = creator)
            it.joinTeamRepository.createJoinTeamRequest(JoinTeamInput(teamId = team.id, assignmentId = assignmentId, composite = composite.id, creatorGitHubUserName = creatorGitHubUserName), creator = creator)
            return@run Result.Success(value = createTeam)
        }
    }

    /**
     * Method to create a request to leave a team
     * Needs a teacher to approve the request
     */
    fun leaveTeamRequest(leaveInfo: LeaveTeamInput, creator: Int): TeamLeaveRequestResponse {
        if (leaveInfo.isNotValid()) return Result.Problem(TeamServicesError.InvalidData)
        return transactionManager.run {
            it.usersRepository.getStudent(creator) ?: return@run Result.Problem(TeamServicesError.InternalError)
            when (it.teamRepository.getTeamById(leaveInfo.teamId)) {
                null -> Result.Problem(TeamServicesError.TeamNotFound)
                else -> {
                    val request = it.leaveTeamRepository.createLeaveTeamRequest(leaveInfo, creator)
                    Result.Success(request)
                }
            }
        }
    }

    /**
     * Method to create a request to join a team
     * Needs a teacher to approve the request
     */
    fun joinTeamRequest(joinInfo: JoinTeamInput, creator: Int): TeamJoinRequestResponse {
        if (joinInfo.isNotValid()) return Result.Problem(TeamServicesError.InvalidData)
        return transactionManager.run {
            it.usersRepository.getStudent(creator) ?: return@run Result.Problem(TeamServicesError.InternalError)
            val assignment = it.assignmentRepository.getAssignmentById(joinInfo.assignmentId)
                ?: return@run Result.Problem(TeamServicesError.AssignmentNotFound)
            val classroom = it.classroomRepository.getClassroomById(assignment.classroomId)
                ?: return@run Result.Problem(TeamServicesError.InternalError)
            if (classroom.isArchived) return@run Result.Problem(TeamServicesError.ClassroomArchived)
            when (it.teamRepository.getTeamById(joinInfo.teamId)) {
                null -> return@run Result.Problem(TeamServicesError.TeamNotFound)
                else -> {
                    val request = it.joinTeamRepository.createJoinTeamRequest(joinInfo, creator)
                    return@run Result.Success(request)
                }
            }
        }
    }

    /**
     * Method to get update the status of a request from a team
     */
    fun updateTeamRequestStatus(requestId: Int, teamId: Int, classroomId: Int): TeamUpdateRequestResponse {
        return transactionManager.run {
            val classroom = it.classroomRepository.getClassroomById(classroomId)
                ?: return@run Result.Problem(TeamServicesError.ClassroomNotFound)
            if (classroom.isArchived) return@run Result.Problem(TeamServicesError.ClassroomArchived)
            when (it.teamRepository.getTeamById(teamId)) {
                null -> return@run Result.Problem(TeamServicesError.TeamNotFound)
                else -> {
                    val request = it.requestRepository.getRequestById(requestId)
                        ?: return@run Result.Problem(TeamServicesError.RequestNotFound)
                    if (request.state != "Rejected") {
                        return@run Result.Problem(TeamServicesError.RequestNotRejected)
                    }
                    if (request.composite != null) {
                        val compositeRequests = it.compositeRepository.getCompositeRequestsById(requestId)
                        if (compositeRequests != null) {
                            compositeRequests.forEach { reqId ->
                                it.requestRepository.changeStateRequest(reqId, "Pending")
                            }
                        } else {
                            return@run Result.Problem(TeamServicesError.RequestNotFound)
                        }
                    } else {
                        it.requestRepository.changeStateRequest(requestId, "Pending")
                    }
                    return@run Result.Success(true)
                }
            }
        }
    }

    /**
     * Method to update the requests of a composite request
     */
    fun updateCreateTeamCompositeRequest(body: UpdateCreateTeamStatusInput, teamId: Int): TeamUpdateStatusResponse {
        return transactionManager.run {
            it.createTeamRepository.updateCreateTeamRequestState(requestId = body.createTeam.requestId, state = body.createTeam.state, githubTeamId = body.createTeam.gitHubTeamId)
            it.joinTeamRepository.updateJoinTeamState(requestId = body.joinTeam.requestId, state = body.joinTeam.state)
            it.createRepoRepository.updateCreateRepoState(requestId = body.createRepo.requestId, state = body.createRepo.state)
            val compositeState = it.compositeRepository.updateCompositeState(compositeId = body.composite.requestId)
            if (compositeState == "Accepted") {
                it.teamRepository.updateTeamStatus(id = teamId)
                it.teamRepository.addStudentToTeam(teamId = teamId, studentId = body.joinTeam.userId)
                it.repoRepository.updateRepoStatus(repoId = body.createRepo.repoId, url = body.createRepo.url ?: "")
            }
            return@run Result.Success(value = true)
        }
    }

    /**
     * Method to post feedback to a team
     */
    fun postFeedback(feedbackInfo: FeedbackInput, classroomId: Int): TeamFeedbackResponse {
        if (feedbackInfo.isNotValid()) return Result.Problem(TeamServicesError.InvalidData)
        return transactionManager.run {
            val classroom = it.classroomRepository.getClassroomById(classroomId) ?: return@run Result.Problem(TeamServicesError.ClassroomNotFound)
            if (classroom.isArchived) return@run Result.Problem(TeamServicesError.ClassroomArchived)
            when (it.teamRepository.getTeamById(feedbackInfo.teamId)) {
                null -> return@run Result.Problem(TeamServicesError.TeamNotFound)
                else -> {
                    val feedback = it.feedbackRepository.createFeedback(feedbackInfo)
                    return@run Result.Success(feedback)
                }
            }
        }
    }

    /**
     * Method to get all the requests of a team
     */
    fun getTeamsRequests(teamId: Int): TeamRequestsResponse {
        return transactionManager.run {
            when (val team = it.teamRepository.getTeamById(teamId)) {
                null -> Result.Problem(TeamServicesError.TeamNotFound)
                else -> {
                    val joinTeam = it.joinTeamRepository.getJoinTeamRequests().filter { teamRequest -> teamRequest.teamId == teamId && teamRequest.state != "Accepted" && teamRequest.composite == null }
                    val leaveTeam = it.leaveTeamRepository.getLeaveTeamRequests().filter { teamRequest -> teamRequest.teamId == teamId }
                    Result.Success(TeamRequestsModel(team, joinTeam, leaveTeam))
                }
            }
        }
    }

    /**
     * Method to get all the requests of a team
     */
    fun getTeamsRequestsForMobile(teamId: Int): TeamRequestsMobileResponse {
        return transactionManager.run {
            when (it.teamRepository.getTeamById(id = teamId)) {
                null -> Result.Problem(value = TeamServicesError.TeamNotFound)
                else -> {
                    val createTeam = it.createTeamRepository.getCreateTeamRequestByTeamId(teamId = teamId) ?: return@run Result.Problem(value = TeamServicesError.TeamNotFound)
                    if (createTeam.state != "Accepted") return@run Result.Problem(value = TeamServicesError.TeamNotAccepted)
                    val joinTeamRequests = it.joinTeamRepository.getJoinTeamRequests().filter { teamRequest -> teamRequest.teamId == teamId }
                    val leaveTeamRequests = it.leaveTeamRepository.getLeaveTeamRequests().filter { teamRequest -> teamRequest.teamId == teamId }
                    val createRepo = it.createRepoRepository.getCreateRepoRequestByCompositeId(compositeId = createTeam.composite) ?: return@run Result.Problem(value = TeamServicesError.InternalError)
                    val joinTeam = joinTeamRequests.find { request -> request.composite == createTeam.composite } ?: return@run Result.Problem(value = TeamServicesError.InternalError)
                    val archiveRepo = it.archiveRepoRepository.getArchiveRepoRequestsByTeam(teamId = teamId)
                    Result.Success(
                        TeamRequestsForMobileModel(
                            needApproval = RequestsThatNeedApproval(
                                joinTeam = joinTeamRequests.filter { request -> request.state != "Accepted" && request.composite == null },
                                leaveTeam = leaveTeamRequests.filter { request -> request.state != "Accepted" && request.composite == null },
                            ),
                            requestsHistory = RequestsHistory(
                                createTeamComposite = CreateTeamComposite(createTeam = createTeam, joinTeam = joinTeam, createRepo = createRepo, compositeState = "Accepted"),
                                joinTeam = joinTeamRequests.filter { request -> request.state == "Accepted" && request.composite == null },
                                leaveTeam = leaveTeamRequests.filter { request -> request.state == "Accepted" && request.composite == null },
                                archiveRepo = if (archiveRepo != null && archiveRepo.state == "Accepted") archiveRepo else null,
                            ),
                        ),
                    )
                }
            }
        }
    }

    fun updateRequestState(requestId: Int, state: String): TeamUpdateRequestResponse {
        if (requestId < 0 || state.isEmpty()) return Result.Problem(TeamServicesError.InvalidData)
        return transactionManager.run {
            it.requestRepository.changeStateRequest(id = requestId, state = state)
            Result.Success(true)
        }
    }

    /**
     * Function to handle the errors
     */
    fun problem(error: TeamServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            is TeamServicesError.RequestNotFound -> Problem.notFound
            is TeamServicesError.TeamNotFound -> Problem.notFound
            is TeamServicesError.RequestNotRejected -> Problem.invalidOperation
            is TeamServicesError.ClassroomNotFound -> Problem.notFound
            is TeamServicesError.ClassroomArchived -> Problem.invalidOperation
            is TeamServicesError.AssignmentNotFound -> Problem.notFound
            is TeamServicesError.InvalidData -> Problem.invalidInput
            is TeamServicesError.InternalError -> Problem.internalError
            is TeamServicesError.TeamNotAccepted -> Problem.invalidOperation
        }
    }
}
