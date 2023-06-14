package isel.ps.classcode.presentation.team

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.dataAccess.gitHubService.GitHubService
import isel.ps.classcode.domain.JoinTeam
import isel.ps.classcode.domain.LeaveRequestStateInput
import isel.ps.classcode.domain.LeaveTeam
import isel.ps.classcode.domain.LeaveTeamWithRepoName
import isel.ps.classcode.domain.TeamRequests
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.team.services.TeamServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.launch

class TeamViewModel(private val teamServices: TeamServices, private val gitHubService: GitHubService) : ViewModel() {
    val teamRequests: TeamRequests?
        get() = _teamRequests
    private var _teamRequests by mutableStateOf<TeamRequests?>(null)

    private var _errorClassCode: HandleClassCodeResponseError? by mutableStateOf(null)
    val errorClassCode: HandleClassCodeResponseError?
        get() = _errorClassCode

    private var _errorGitHub: HandleGitHubResponseError? by mutableStateOf(null)

    val errorGitHub: HandleGitHubResponseError?
        get() = _errorGitHub

    lateinit var teamInfo: TeamAndOtherInfo

    fun getTeamRequests() = viewModelScope.launch {
        when (val requests = teamServices.getTeamRequests(courseId = teamInfo.courseId, classroomId = teamInfo.classroomId, assignmentId = teamInfo.team.assignment, teamId = teamInfo.team.id)) {
            is Either.Right -> {
                _teamRequests = requests.value
            }
            is Either.Left -> {
                _errorClassCode = requests.value
            }
        }
    }

    fun addMemberToTeam(joinTeam: JoinTeam) = viewModelScope.launch {
        when (val result = gitHubService.addMemberToTeamInGitHub(orgName = teamInfo.courseName, username = joinTeam.githubUsername, teamSlug = teamInfo.team.name)) {
            is Either.Right -> {
                when (val res = teamServices.updateStateOfRequestInClassCode(courseId = teamInfo.courseId, classroomId = teamInfo.classroomId, assignmentId = teamInfo.team.assignment, teamId = joinTeam.teamId, requestId = joinTeam.requestId, state = "Accepted", isJoinTeam = true, creator = joinTeam.creator)) {
                    is Either.Right -> {
                        val requests = _teamRequests
                        if (requests != null) {
                            _teamRequests = requests.copy(
                                needApproval = requests.needApproval.copy(
                                    joinTeam = requests.needApproval.joinTeam.filter { it.requestId != joinTeam.requestId },
                                ),
                                requestsHistory = requests.requestsHistory.copy(
                                    joinTeam = requests.requestsHistory.joinTeam + joinTeam.copy(state = "Accepted"),
                                ),
                            )
                        }
                    }
                    is Either.Left -> {
                        _errorClassCode = res.value
                    }
                }
            }
            is Either.Left -> {
                _errorGitHub = result.value
            }
        }
    }

    fun removeMemberFromTeam(leaveTeam: LeaveTeamWithRepoName, activity: Activity) = viewModelScope.launch {
        val request = leaveTeam.leaveTeam
        if (leaveTeam.leaveTeam.membersCount == 1) {
            when (val resultArc = gitHubService.archiveRepoInGithub(orgName = teamInfo.courseName, repoName = leaveTeam.repoName)) {
                is Either.Right -> {
                    // Do Nothing
                }
                is Either.Left -> {
                    _errorGitHub = resultArc.value
                }
            }
            when (val result = gitHubService.deleteTeamFromTeamInGitHub(courseName = teamInfo.courseName, teamSlug = teamInfo.team.name)) {
                is Either.Right -> {
                    when (val res = teamServices.deleteTeamInClassCode(courseId = teamInfo.courseId, classroomId = teamInfo.classroomId, assignmentId = teamInfo.team.assignment, teamId = request.teamId, leaveRequestStateInput = LeaveRequestStateInput(requestId = request.requestId))) {
                        is Either.Right -> activity.finish()
                        is Either.Left -> _errorClassCode = res.value
                    }
                }
                is Either.Left -> _errorGitHub = result.value
            }
        } else {
            when (
                val result = gitHubService.removeMemberFromTeamInGitHub(
                    leaveTeam = request,
                    courseName = teamInfo.courseName,
                    teamSlug = teamInfo.team.name,
                )
            ) {
                is Either.Right -> {
                    when (
                        val res = teamServices.updateStateOfRequestInClassCode(
                            courseId = teamInfo.courseId,
                            classroomId = teamInfo.classroomId,
                            assignmentId = teamInfo.team.assignment,
                            teamId = request.teamId,
                            requestId = request.requestId,
                            state = "Accepted",
                            isJoinTeam = false,
                            creator = request.creator,
                        )
                    ) {
                        is Either.Right -> {
                            val requests = _teamRequests
                            if (requests != null) {
                                _teamRequests = requests.copy(
                                    needApproval = requests.needApproval.copy(
                                        leaveTeam = requests.needApproval.leaveTeam
                                            .filter { it.leaveTeam.requestId != request.requestId }
                                            .map {
                                                LeaveTeamWithRepoName(repoName = it.repoName, leaveTeam = it.leaveTeam.copy(membersCount = it.leaveTeam.membersCount - 1))
                                            },
                                    ),
                                    requestsHistory = requests.requestsHistory.copy(
                                        leaveTeam = requests.requestsHistory.leaveTeam + LeaveTeamWithRepoName(repoName = leaveTeam.repoName, leaveTeam = request.copy(state = "Accepted")),
                                    ),
                                )
                            }
                        }

                        is Either.Left -> {
                            _errorClassCode = res.value
                        }
                    }
                }

                is Either.Left -> {
                    _errorGitHub = result.value
                }
            }
        }
    }

    fun rejectJoinTeamRequest(joinTeam: JoinTeam) = viewModelScope.launch {
        when (val result = teamServices.updateStateOfRequestInClassCode(courseId = teamInfo.courseId, classroomId = teamInfo.classroomId, assignmentId = teamInfo.team.assignment, teamId = joinTeam.teamId, requestId = joinTeam.requestId, state = "Rejected", isJoinTeam = true, creator = joinTeam.creator)) {
            is Either.Right -> {
                getTeamRequests()
            }
            is Either.Left -> {
                _errorClassCode = result.value
            }
        }
    }

    fun rejectLeaveTeamRequest(leaveTeam: LeaveTeam) = viewModelScope.launch {
        when (val result = teamServices.updateStateOfRequestInClassCode(courseId = teamInfo.courseId, classroomId = teamInfo.classroomId, assignmentId = teamInfo.team.assignment, teamId = leaveTeam.teamId, requestId = leaveTeam.requestId, state = "Rejected", isJoinTeam = false, creator = leaveTeam.creator)) {
            is Either.Right -> {
                getTeamRequests()
            }
            is Either.Left -> {
                _errorClassCode = result.value
            }
        }
    }
}
