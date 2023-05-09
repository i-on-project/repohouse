package com.isel.leic.ps.ionClassCode.services

import com.isel.leic.ps.ionClassCode.domain.Feedback
import com.isel.leic.ps.ionClassCode.domain.input.FeedbackInput
import com.isel.leic.ps.ionClassCode.domain.input.TeamInput
import com.isel.leic.ps.ionClassCode.domain.input.request.CreateRepoInput
import com.isel.leic.ps.ionClassCode.domain.input.request.CreateTeamInput
import com.isel.leic.ps.ionClassCode.domain.input.request.JoinTeamInput
import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveTeamInput
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
                Result.Problem(TeamServicesError.TeamNotFound)
            } else {
                val students = it.teamRepository.getStudentsFromTeam(teamId)
                val repos = it.repoRepository.getReposByTeam(teamId)
                val feedbacks = it.feedbackRepository.getFeedbacksByTeam(teamId)
                Result.Success(TeamModel(team, students, repos, feedbacks))
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
                        students = it.teamRepository.getStudentsFromTeam(teamId = team.id),
                        repos = it.repoRepository.getReposByTeam(teamId = team.id),
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
    fun createTeamRequest(createTeamInfo: CreateTeamInput, creator: Int, assignmentId: Int, classroomId: Int): TeamCreateRequestResponse {
        if (createTeamInfo.isNotValid()) return Result.Problem(TeamServicesError.InvalidData)
        return transactionManager.run {
            it.usersRepository.getStudent(creator) ?: return@run Result.Problem(TeamServicesError.InternalError)
            val classroom = it.classroomRepository.getClassroomById(classroomId) ?: return@run Result.Problem(TeamServicesError.ClassroomNotFound)
            it.assignmentRepository.getAssignmentById(assignmentId) ?: return@run Result.Problem(TeamServicesError.AssignmentNotFound)
            if (classroom.isArchived) return@run Result.Problem(TeamServicesError.ClassroomArchived)
            val teamRequest = it.createTeamRepository.createCreateTeamRequest(createTeamInfo, creator)
            val team = it.teamRepository.createTeam(
                team = TeamInput(
                    "${classroom.name} - $assignmentId - ${teamRequest.id}",
                    assignmentId,
                    false,
                ),
            )
            it.createRepoRepository.createCreateRepoRequest(CreateRepoInput(team.id), creator)
            Result.Success(CreateTeam(team.id, creator))
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
                        val compositeRequests = it.compositeRepository.getCompositeRequestById(requestId)
                        if (compositeRequests != null) {
                            compositeRequests.requests.forEach { reqId ->
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
                    val joinTeam = it.joinTeamRepository.getJoinTeamRequests().filter { teamRequest -> teamRequest.teamId == teamId }
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
            is TeamServicesError.TeamNotFound -> Problem.notFound
            is TeamServicesError.RequestNotRejected -> Problem.invalidOperation
            is TeamServicesError.RequestNotFound -> Problem.notFound
            is TeamServicesError.ClassroomArchived -> Problem.invalidOperation
            is TeamServicesError.ClassroomNotFound -> Problem.notFound
            is TeamServicesError.AssignmentNotFound -> Problem.notFound
            is TeamServicesError.InvalidData -> Problem.invalidInput
            is TeamServicesError.InternalError -> Problem.internalError
        }
    }
}
