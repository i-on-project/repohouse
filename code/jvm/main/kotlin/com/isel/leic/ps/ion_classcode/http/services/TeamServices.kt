package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.input.FeedbackInput
import com.isel.leic.ps.ion_classcode.domain.input.request.CreateTeamInput
import com.isel.leic.ps.ion_classcode.domain.input.request.JoinTeamInput
import com.isel.leic.ps.ion_classcode.domain.input.request.LeaveTeamInput
import com.isel.leic.ps.ion_classcode.domain.requests.Composite
import com.isel.leic.ps.ion_classcode.http.model.output.TeamOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeamRequestsOutputModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias TeamResponse = Either<TeamServicesError, TeamOutputModel>
typealias TeamRequestsResponse = Either<TeamServicesError, TeamRequestsOutputModel>
typealias TeamCreateRequestResponse = Either<TeamServicesError, TeamOutputModel>
typealias TeamLeaveRequestResponse = Either<TeamServicesError, Int>
typealias TeamJoinRequestResponse = Either<TeamServicesError, Int>
typealias TeamUpdateRequestResponse = Either<TeamServicesError, Boolean>
typealias TeamFeedbackResponse = Either<TeamServicesError, Int>

sealed class TeamServicesError{
    object RequestNotFound: TeamServicesError()

    object TeamNotFound: TeamServicesError()
    object RequestNotRejected: TeamServicesError()
}

@Component
class TeamServices(
    val transactionManager: TransactionManager,
) {

    fun getTeamInfo(teamId:Int):TeamResponse{
        return transactionManager.run {
            val team = it.teamRepository.getTeamById(teamId)
            if(team == null) Either.Left(TeamServicesError.TeamNotFound)
            else {
                val students = it.teamRepository.getStudentsFromTeam(teamId)
                val repos = it.repoRepository.getReposByTeam(teamId)
                val feedbacks = it.feedbackRepository.getFeedbacksByTeam(teamId)
                Either.Right(TeamOutputModel(team, students, repos, feedbacks))
            }
        }
    }

    fun createTeamRequest(createTeamInfo: CreateTeamInput):TeamCreateRequestResponse{
        return transactionManager.run {
            val teamId = it.createTeamRepository.createCreateTeamRequest(createTeamInfo)
            val team = it.teamRepository.getTeamById(teamId)
            if(team == null) Either.Left(TeamServicesError.TeamNotFound)
            else {
                val students = it.teamRepository.getStudentsFromTeam(teamId)
                val repos = it.repoRepository.getReposByTeam(teamId)
                val feedbacks = it.feedbackRepository.getFeedbacksByTeam(teamId)
                Either.Right(TeamOutputModel(team, students, repos, feedbacks))
            }
        }
    }

    fun leaveTeamRequest(leaveInfo: LeaveTeamInput): TeamLeaveRequestResponse {
        return transactionManager.run {
            when( it.teamRepository.getTeamById(leaveInfo.teamId)){
                null -> Either.Left(TeamServicesError.TeamNotFound)
                else -> {
                    val request = it.leaveTeamRepository.createLeaveTeamRequest(leaveInfo)
                    Either.Right(request)
                }
            }
        }
    }

    fun joinTeamRequest(joinInfo:JoinTeamInput):TeamJoinRequestResponse{
        return transactionManager.run {
            when(it.teamRepository.getTeamById(joinInfo.teamId)){
                null -> Either.Left(TeamServicesError.TeamNotFound)
                else -> {
                    val request = it.joinTeamRepository.createJoinTeamRequest(joinInfo)
                    Either.Right(request)
                }
            }
        }
    }

    fun updateTeamRequestStatus(requestId: Int, teamId: Int): TeamUpdateRequestResponse {
        return transactionManager.run {
            when (it.teamRepository.getTeamById(teamId)){
                null -> Either.Left(TeamServicesError.TeamNotFound)
                else -> {
                    val request = it.requestRepository.getRequestById(requestId)
                        ?: return@run Either.Left(TeamServicesError.RequestNotFound)
                    if (request.state != "Rejected") {
                        Either.Left(TeamServicesError.RequestNotRejected)
                    }
                    val compositeRequests = it.compositeRepository.getCompositeRequestById(id = requestId)
                    if (compositeRequests != null) {
                        compositeRequests.requests.forEach {reqId ->
                            it.requestRepository.changeStateRequest(reqId, "Pending")
                        }
                    }else{
                        it.requestRepository.changeStateRequest(requestId, "Pending")
                    }
                    Either.Right(true)
                }
            }
        }
    }

    fun postFeedback(feedbackInfo: FeedbackInput): TeamFeedbackResponse {
        return transactionManager.run {
            when(it.teamRepository.getTeamById(feedbackInfo.teamId)){
                null -> Either.Left(TeamServicesError.TeamNotFound)
                else -> {
                    val feedback =it.feedbackRepository.createFeedback(feedbackInfo)
                    Either.Right(feedback)
                }
            }
        }
    }

    fun getTeamsRequests(teamId: Int): TeamRequestsResponse {
        return transactionManager.run {
            when(val team = it.teamRepository.getTeamById(teamId)){
                null -> Either.Left(TeamServicesError.TeamNotFound)
                else -> {
                    val joinTeam = it.joinTeamRepository.getJoinTeamRequests().filter { teamRequest -> teamRequest.teamId == teamId }
                    val leaveTeam = it.leaveTeamRepository.getLeaveTeamRequests().filter { teamRequest -> teamRequest.teamId == teamId }
                    Either.Right(TeamRequestsOutputModel(team, joinTeam, leaveTeam))
                }
            }
        }
    }
}
