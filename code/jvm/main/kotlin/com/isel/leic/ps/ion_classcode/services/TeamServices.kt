package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.Feedback
import com.isel.leic.ps.ion_classcode.domain.input.FeedbackInput
import com.isel.leic.ps.ion_classcode.domain.input.request.CompositeInput
import com.isel.leic.ps.ion_classcode.domain.input.request.CreateRepoInput
import com.isel.leic.ps.ion_classcode.domain.input.request.CreateTeamInput
import com.isel.leic.ps.ion_classcode.domain.input.request.JoinTeamInput
import com.isel.leic.ps.ion_classcode.domain.input.request.LeaveTeamInput
import com.isel.leic.ps.ion_classcode.domain.requests.CreateTeam
import com.isel.leic.ps.ion_classcode.domain.requests.JoinTeam
import com.isel.leic.ps.ion_classcode.domain.requests.LeaveTeam
import com.isel.leic.ps.ion_classcode.http.model.output.TeamModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeamRequestsModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Result
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
        if (teamId <= 0) return Result.Problem(value = TeamServicesError.InvalidData)
        return transactionManager.run {
            val team = it.teamRepository.getTeamById(id = teamId)
            if (team == null) {
                Result.Problem(value = TeamServicesError.TeamNotFound)
            } else {
                val students = it.teamRepository.getStudentsFromTeam(teamId = teamId)
                val repos = it.repoRepository.getReposByTeam(teamId = teamId)
                val feedbacks = it.feedbackRepository.getFeedbacksByTeam(teamId = teamId)
                Result.Success(value = TeamModel(team = team, students = students, repos = repos, feedbacks = feedbacks))
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
        if (assignmentId <= 0 || classroomId <= 0 || createTeamInfo.isNotValid()) return Result.Problem(value = TeamServicesError.InvalidData)
        return transactionManager.run {
            val classroom = it.classroomRepository.getClassroomById(classroomId = classroomId)
                ?: return@run Result.Problem(value = TeamServicesError.ClassroomNotFound)
            if (classroom.isArchived) return@run Result.Problem(value = TeamServicesError.ClassroomArchived)

            val team = it.createTeamRepository.createCreateTeamRequest(request = createTeamInfo, creator = creator)
            // Join all requests in one composite request
            val composite = it.compositeRepository.createCompositeRequest(
                CompositeInput(requests = listOf(team.id)),
                creator = creator,
            )
            it.joinTeamRepository.createJoinTeamRequest(JoinTeamInput(assignmentId = assignmentId, teamId = team.id, composite = composite.id), creator = creator)
            it.createRepoRepository.createCreateRepoRequest(CreateRepoInput(teamId = team.id, composite = composite.id), creator = creator)

            Result.Success(value = team)
        }
    }

    /**
     * Method to create a request to leave a team
     * Needs a teacher to approve the request
     */
    fun leaveTeamRequest(leaveInfo: LeaveTeamInput, creator: Int): TeamLeaveRequestResponse {
        if (leaveInfo.isNotValid()) return Result.Problem(value = TeamServicesError.InvalidData)
        return transactionManager.run {
            when (it.teamRepository.getTeamById(id = leaveInfo.teamId)) {
                null -> Result.Problem(value = TeamServicesError.TeamNotFound)
                else -> {
                    val request = it.leaveTeamRepository.createLeaveTeamRequest(request = leaveInfo, creator = creator)
                    Result.Success(value = request)
                }
            }
        }
    }

    /**
     * Method to create a request to join a team
     * Needs a teacher to approve the request
     */
    fun joinTeamRequest(joinInfo: JoinTeamInput, creator: Int): TeamJoinRequestResponse {
        if (joinInfo.isNotValid()) return Result.Problem(value = TeamServicesError.InvalidData)
        return transactionManager.run {
            val assignment = it.assignmentRepository.getAssignmentById(assignmentId = joinInfo.assignmentId)
                ?: return@run Result.Problem(value = TeamServicesError.AssignmentNotFound)
            val classroom = it.classroomRepository.getClassroomById(classroomId = assignment.classroomId)
                ?: return@run Result.Problem(value = TeamServicesError.ClassroomNotFound)
            if (classroom.isArchived) return@run Result.Problem(value = TeamServicesError.ClassroomArchived)
            when (it.teamRepository.getTeamById(id = joinInfo.teamId)) {
                null -> Result.Problem(value = TeamServicesError.TeamNotFound)
                else -> {
                    val request = it.joinTeamRepository.createJoinTeamRequest(request = joinInfo, creator = creator)
                    Result.Success(value = request)
                }
            }
        }
    }

    /**
     * Method to get update the status of a request from a team
     */
    fun updateTeamRequestStatus(requestId: Int, teamId: Int, classroomId: Int): TeamUpdateRequestResponse {
        if (requestId <= 0 || teamId <= 0 || classroomId <= 0) return Result.Problem(value = TeamServicesError.InvalidData)
        return transactionManager.run {
            val classroom = it.classroomRepository.getClassroomById(classroomId = classroomId)
                ?: return@run Result.Problem(value = TeamServicesError.ClassroomNotFound)
            if (classroom.isArchived) return@run Result.Problem(value = TeamServicesError.ClassroomArchived)
            when (it.teamRepository.getTeamById(id = teamId)) {
                null -> Result.Problem(value = TeamServicesError.TeamNotFound)
                else -> {
                    val request = it.requestRepository.getRequestById(id = requestId)
                        ?: return@run Result.Problem(value = TeamServicesError.RequestNotFound)
                    if (request.state != "Rejected") {
                        return@run Result.Problem(value = TeamServicesError.RequestNotRejected)
                    }
                    if (request.composite != null) {
                        val compositeRequests = it.compositeRepository.getCompositeRequestById(id = requestId)
                        if (compositeRequests != null) {
                            compositeRequests.requests.forEach { reqId ->
                                it.requestRepository.changeStateRequest(id = reqId, state = "Pending")
                            }
                        } else {
                            Result.Problem(value = TeamServicesError.RequestNotFound)
                        }
                    } else {
                        it.requestRepository.changeStateRequest(id = requestId, state = "Pending")
                    }
                    Result.Success(value = true)
                }
            }
        }
    }

    /**
     * Method to post a feedback to a team
     */
    fun postFeedback(feedbackInfo: FeedbackInput, classroomId: Int): TeamFeedbackResponse {
        if (feedbackInfo.isNotValid() || classroomId <= 0) return Result.Problem(value = TeamServicesError.InvalidData)
        return transactionManager.run {
            val classroom = it.classroomRepository.getClassroomById(classroomId = classroomId)
                ?: return@run Result.Problem(value = TeamServicesError.ClassroomNotFound)
            if (classroom.isArchived) return@run Result.Problem(value = TeamServicesError.ClassroomArchived)
            when (it.teamRepository.getTeamById(id = feedbackInfo.teamId)) {
                null -> Result.Problem(value = TeamServicesError.TeamNotFound)
                else -> {
                    val feedback = it.feedbackRepository.createFeedback(feedback = feedbackInfo)
                    Result.Success(value = feedback)
                }
            }
        }
    }

    /**
     * Method to get all the requests of a team
     */
    fun getTeamsRequests(teamId: Int): TeamRequestsResponse {
        if (teamId <= 0) return Result.Problem(value = TeamServicesError.InvalidData)
        return transactionManager.run {
            when (val team = it.teamRepository.getTeamById(id = teamId)) {
                null -> Result.Problem(value = TeamServicesError.TeamNotFound)
                else -> {
                    val joinTeam = it.joinTeamRepository.getJoinTeamRequests().filter { teamRequest -> teamRequest.teamId == teamId }
                    val leaveTeam = it.leaveTeamRepository.getLeaveTeamRequests().filter { teamRequest -> teamRequest.teamId == teamId }
                    Result.Success(value = TeamRequestsModel(team = team, joinTeam = joinTeam, leaveTeam = leaveTeam))
                }
            }
        }
    }
}
