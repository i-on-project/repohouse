package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.input.FeedbackInput
import com.isel.leic.ps.ion_classcode.domain.input.request.CompositeInput
import com.isel.leic.ps.ion_classcode.domain.input.request.CreateRepoInput
import com.isel.leic.ps.ion_classcode.domain.input.request.CreateTeamInput
import com.isel.leic.ps.ion_classcode.domain.input.request.JoinTeamInput
import com.isel.leic.ps.ion_classcode.domain.input.request.LeaveTeamInput
import com.isel.leic.ps.ion_classcode.http.model.output.TeamModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeamRequestsModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias TeamResponse = Either<TeamServicesError, TeamModel>
typealias TeamRequestsResponse = Either<TeamServicesError, TeamRequestsModel>
typealias TeamCreateRequestResponse = Either<TeamServicesError, TeamModel>
typealias TeamLeaveRequestResponse = Either<TeamServicesError, Int>
typealias TeamJoinRequestResponse = Either<TeamServicesError, Int>
typealias TeamUpdateRequestResponse = Either<TeamServicesError, Boolean>
typealias TeamFeedbackResponse = Either<TeamServicesError, Int>

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
        if (teamId <= 0) return Either.Left(value = TeamServicesError.InvalidData)
        return transactionManager.run {
            val team = it.teamRepository.getTeamById(id = teamId)
            if (team == null) {
                Either.Left(value = TeamServicesError.TeamNotFound)
            } else {
                val students = it.teamRepository.getStudentsFromTeam(teamId = teamId)
                val repos = it.repoRepository.getReposByTeam(teamId = teamId)
                val feedbacks = it.feedbackRepository.getFeedbacksByTeam(teamId = teamId)
                Either.Right(value = TeamModel(team = team, students = students, repos = repos, feedbacks = feedbacks))
            }
        }
    }

    /**
     * Method to create a request to create a team
     * Needs a teacher to approve the request
     */
    fun createTeamRequest(createTeamInfo: CreateTeamInput, assigmentId: Int, classroomId: Int): TeamCreateRequestResponse {
        if (assigmentId <= 0 || classroomId <= 0 || createTeamInfo.isNotValid()) return Either.Left(value = TeamServicesError.InvalidData)
        return transactionManager.run {
            val classroom = it.classroomRepository.getClassroomById(classroomId = classroomId)
                ?: return@run Either.Left(value = TeamServicesError.ClassroomNotFound)
            if (classroom.isArchived) return@run Either.Left(value = TeamServicesError.ClassroomArchived)

            val teamId = it.createTeamRepository.createCreateTeamRequest(request = createTeamInfo)
            val team = it.teamRepository.getTeamById(id = teamId)
            if (team == null) {
                Either.Left(value = TeamServicesError.TeamNotFound)
            } else {
                // Join all requests in one composite request
                val composite = it.compositeRepository.createCompositeRequest(
                    CompositeInput(listOf(teamId), createTeamInfo.creator))
                it.joinTeamRepository.createJoinTeamRequest(JoinTeamInput(assigmentId,teamId,composite, createTeamInfo.creator))
                it.createRepoRepository.createCreateRepoRequest(CreateRepoInput(teamId,composite, createTeamInfo.creator))

                // Get all info about the team
                val students = it.teamRepository.getStudentsFromTeam(teamId)
                val repos = it.repoRepository.getReposByTeam(teamId)
                val feedbacks = it.feedbackRepository.getFeedbacksByTeam(teamId)
                Either.Right(TeamModel(team, students, repos, feedbacks))
            }
        }
    }

    /**
     * Method to create a request to leave a team
     * Needs a teacher to approve the request
     */
    fun leaveTeamRequest(leaveInfo: LeaveTeamInput): TeamLeaveRequestResponse {
        return transactionManager.run {
            when (it.teamRepository.getTeamById(id = leaveInfo.teamId)) {
                null -> Either.Left(value = TeamServicesError.TeamNotFound)
                else -> {
                    val request = it.leaveTeamRepository.createLeaveTeamRequest(request = leaveInfo)
                    Either.Right(value = request)
                }
            }
        }
    }

    /**
     * Method to create a request to join a team
     * Needs a teacher to approve the request
     */
    fun joinTeamRequest(joinInfo: JoinTeamInput): TeamJoinRequestResponse {
        if(joinInfo.isNotValid()) return Either.Left(value = TeamServicesError.InvalidData)
        return transactionManager.run {
            val assignment = it.assignmentRepository.getAssignmentById(assignmentId = joinInfo.assignmentId)
                ?: return@run Either.Left(value = TeamServicesError.AssignmentNotFound)
            val classroom = it.classroomRepository.getClassroomById(classroomId = assignment.classroomId)
                ?: return@run Either.Left(value = TeamServicesError.ClassroomNotFound)
            if (classroom.isArchived) return@run Either.Left(value = TeamServicesError.ClassroomArchived)
            when (it.teamRepository.getTeamById(id = joinInfo.teamId)) {
                null -> Either.Left(value = TeamServicesError.TeamNotFound)
                else -> {
                    val request = it.joinTeamRepository.createJoinTeamRequest(request = joinInfo)
                    Either.Right(value = request)
                }
            }
        }
    }

    /**
     * Method to get update the status of a request from a team
     */
    fun updateTeamRequestStatus(requestId: Int, teamId: Int, classroomId: Int): TeamUpdateRequestResponse {
        if (requestId <= 0 || teamId <= 0 || classroomId <= 0) return Either.Left(value = TeamServicesError.InvalidData)
        return transactionManager.run {
            val classroom = it.classroomRepository.getClassroomById(classroomId = classroomId)
                ?: return@run Either.Left(value = TeamServicesError.ClassroomNotFound)
            if (classroom.isArchived) return@run Either.Left(value = TeamServicesError.ClassroomArchived)
            when (it.teamRepository.getTeamById(id = teamId)) {
                null -> Either.Left(value = TeamServicesError.TeamNotFound)
                else -> {
                    val request = it.requestRepository.getRequestById(id = requestId)
                        ?: return@run Either.Left(value = TeamServicesError.RequestNotFound)
                    if (request.state != "Rejected") {
                        Either.Left(value = TeamServicesError.RequestNotRejected)
                    }
                    val compositeRequests = it.compositeRepository.getCompositeRequestById(id = requestId)
                    if (compositeRequests != null) {
                        compositeRequests.requests.forEach { reqId ->
                            it.requestRepository.changeStateRequest(id = reqId, state = "Pending")
                        }
                    } else {
                        it.requestRepository.changeStateRequest(id = requestId, state = "Pending")
                    }
                    Either.Right(value = true)
                }
            }
        }
    }

    /**
     * Method to post a feedback to a team
     */
    fun postFeedback(feedbackInfo: FeedbackInput, classroomId: Int): TeamFeedbackResponse {
        if (feedbackInfo.isNotValid() || classroomId <= 0) return Either.Left(value = TeamServicesError.InvalidData)
        return transactionManager.run {
            val classroom = it.classroomRepository.getClassroomById(classroomId = classroomId)
                ?: return@run Either.Left(value = TeamServicesError.ClassroomNotFound)
            if (classroom.isArchived) return@run Either.Left(value = TeamServicesError.ClassroomArchived)
            when (it.teamRepository.getTeamById(id = feedbackInfo.teamId)) {
                null -> Either.Left(value = TeamServicesError.TeamNotFound)
                else -> {
                    val feedback = it.feedbackRepository.createFeedback(feedback = feedbackInfo)
                    Either.Right(value = feedback)
                }
            }
        }
    }

    /**
     * Method to get all the requests of a team
     */
    fun getTeamsRequests(teamId: Int): TeamRequestsResponse {
        if (teamId <= 0) return Either.Left(value = TeamServicesError.InvalidData)
        return transactionManager.run {
            when (val team = it.teamRepository.getTeamById(id = teamId)) {
                null -> Either.Left(value = TeamServicesError.TeamNotFound)
                else -> {
                    val joinTeam = it.joinTeamRepository.getJoinTeamRequests().filter { teamRequest -> teamRequest.teamId == teamId }
                    val leaveTeam = it.leaveTeamRepository.getLeaveTeamRequests().filter { teamRequest -> teamRequest.teamId == teamId }
                    Either.Right(value = TeamRequestsModel(team = team, joinTeam = joinTeam, leaveTeam = leaveTeam))
                }
            }
        }
    }
}
