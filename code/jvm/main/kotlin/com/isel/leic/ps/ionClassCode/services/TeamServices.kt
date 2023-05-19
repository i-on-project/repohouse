package com.isel.leic.ps.ionClassCode.services

import com.isel.leic.ps.ionClassCode.domain.Feedback
import com.isel.leic.ps.ionClassCode.domain.StudentWithoutToken
import com.isel.leic.ps.ionClassCode.domain.input.FeedbackInput
import com.isel.leic.ps.ionClassCode.domain.input.RepoInput
import com.isel.leic.ps.ionClassCode.domain.input.TeamInput
import com.isel.leic.ps.ionClassCode.domain.input.UpdateCreateTeamStatusInput
import com.isel.leic.ps.ionClassCode.domain.input.request.*
import com.isel.leic.ps.ionClassCode.domain.requests.CreateTeam
import com.isel.leic.ps.ionClassCode.domain.requests.JoinTeam
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveTeam
import com.isel.leic.ps.ionClassCode.http.model.output.TeamModel
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
    fun createTeamRequest(creator: Int, assignmentId: Int, classroomId: Int): TeamCreateRequestResponse {
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
            val name = "${classroom.name} - $assignmentId - ${team.id}"
            val repo = it.repoRepository.createRepo(repo = RepoInput(name = "${classroom.name} - $assignmentId - ${team.id}", url = null, teamId = team.id))
            val composite = it.compositeRepository.createCompositeRequest(request = CompositeInput(), creator = creator)
            val createTeam = it.createTeamRepository.createCreateTeamRequest(request = CreateTeamInput(teamId = team.id, composite = composite.id), creator = creator)
            it.createRepoRepository.createCreateRepoRequest(request = CreateRepoInput(repoId = repo.id, composite = composite.id), creator = creator)
            it.joinTeamRepository.createJoinTeamRequest(JoinTeamInput(teamId = team.id, assignmentId = assignmentId, composite = composite.id), creator = creator)
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

    fun updateCreateTeamCompositeRequest(body: UpdateCreateTeamStatusInput, teamId: Int): TeamUpdateStatusResponse {
        return transactionManager.run {
            it.compositeRepository.updateCompositeState(requestId = body.composite.requestId, state = body.composite.state)
            it.createTeamRepository.updateCreateTeamRequestState(requestId = body.createTeam.requestId, state = body.createTeam.state)
            if (body.createTeam.state == "Accepted") {
                it.teamRepository.updateTeamStatus(id = teamId)
            }
            it.joinTeamRepository.updateJoinTeamState(requestId = body.joinTeam.requestId, state = body.joinTeam.state)
            if (body.joinTeam.state == "Accepted") {
                it.teamRepository.addStudentToTeam(teamId = teamId, studentId = body.joinTeam.userId)
            }
            it.createRepoRepository.updateCreateRepoState(requestId = body.createRepo.requestId, state = body.createRepo.state)
            if (body.createRepo.state == "Accepted") {
                it.repoRepository.updateRepoStatus(repoId = body.createRepo.repoId, url = body.createRepo.url)
            }
            return@run Result.Success(value = true)
        }
    }

    /**
     * Method to post a feedback to a team
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
        }
    }
}
