package isel.ps.classcode.presentation.classroom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.CreateTeamComposite
import isel.ps.classcode.domain.Team
import isel.ps.classcode.domain.UpdateCompositeState
import isel.ps.classcode.domain.UpdateCreateRepoState
import isel.ps.classcode.domain.UpdateCreateTeamRequestState
import isel.ps.classcode.domain.UpdateCreateTeamStatusInput
import isel.ps.classcode.domain.UpdateJoinTeamState
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.classroom.services.ClassroomServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.launch

class ClassroomViewModel(private val classroomServices: ClassroomServices) : ViewModel() {

    val assignments: List<Assignment>?
        get() = _assignments
    private var _assignments by mutableStateOf<List<Assignment>?>(null)

    val assignment: Assignment?
        get() = _assignment
    private var _assignment by mutableStateOf<Assignment?>(null)
    val teamsCreated: List<Team>?
        get() = _teamsCreated
    private var _teamsCreated by mutableStateOf<List<Team>?>(null)

    val createTeamComposite: List<CreateTeamComposite>?
        get() = _createTeamComposite
    private var _createTeamComposite by mutableStateOf<List<CreateTeamComposite>?>(null)

    private var _errorClassCode: HandleClassCodeResponseError? by mutableStateOf(null)
    val errorClassCode: HandleClassCodeResponseError?
        get() = _errorClassCode

    private var _errorGitHub: HandleGitHubResponseError? by mutableStateOf(null)
    val errorGitHub: HandleGitHubResponseError?
        get() = _errorGitHub

    fun getAssignments(classroomId: Int, courseId: Int) = viewModelScope.launch {
        when (val assignments = classroomServices.getAssignments(classroomId = classroomId, courseId = courseId)) {
            is Either.Right -> {
                if (assignments.value.isNotEmpty()) {
                    _assignments = assignments.value
                    _assignment = assignments.value.first()
                    getTeams(classroomId = classroomId, courseId = courseId, assignmentId = assignments.value.first().id)
                } else {
                    _assignments = emptyList()
                    _teamsCreated = emptyList()
                    _createTeamComposite = emptyList()
                    _assignment = null
                }
            }
            is Either.Left -> {
                _errorClassCode = assignments.value
            }
        }
    }

    fun getTeams(classroomId: Int, courseId: Int, assignmentId: Int) = viewModelScope.launch {
        when (val teams = classroomServices.getTeams(classroomId = classroomId, courseId = courseId, assignmentId = assignmentId)) {
            is Either.Right -> {
                _teamsCreated = teams.value.teamsCreated
                _createTeamComposite = teams.value.createTeamComposite
            }
            is Either.Left -> { _errorClassCode = teams.value }
        }
    }

    fun createTeamCompositeAccepted(team: CreateTeamComposite, courseName: String, classroomId: Int, courseId: Int, assignmentId: Int) = viewModelScope.launch {
        val teamSlug = team.createTeam.teamName.replace(" ", "-")
        val createTeamResult = if (team.createTeam.state == "Accepted") null else classroomServices.createTeamInGitHub(createTeamComposite = team, orgName = courseName)
        val githubTeamId = if (createTeamResult == null) team.createTeam.gitHubTeamId else createTeamResult.value
        val createRepoResult = if (team.createRepo.state == "Accepted") {
            null
        } else {
            if (createTeamResult == null) {
                classroomServices.createRepoInGitHub(orgName = courseName, repo = team.createRepo, teamId = githubTeamId)
            } else {
                classroomServices.createRepoInGitHub(orgName = courseName, repo = team.createRepo, teamId = createTeamResult.value)
            }
        }
        val joinTeamResult = if (team.joinTeam.state == "Accepted") null else classroomServices.addMemberToTeamInGitHub(orgName = courseName, teamSlug = teamSlug, username = team.joinTeam.githubUsername)
        val updateRequest = UpdateCreateTeamStatusInput(
            composite = UpdateCompositeState(requestId = team.createTeam.composite),
            createTeam = UpdateCreateTeamRequestState(requestId = team.createTeam.requestId, state = getState(res = createTeamResult?.isCompleted), gitHubTeamId = githubTeamId),
            createRepo = UpdateCreateRepoState(requestId = team.createRepo.requestId, state = getState(res = createRepoResult?.isCompleted), url = createRepoResult?.value, repoId = team.createRepo.repoId),
            joinTeam = UpdateJoinTeamState(requestId = team.joinTeam.requestId, state = getState(res = joinTeamResult?.isCompleted), userId = team.joinTeam.creator),
        )
        when (val res = classroomServices.changeCreateTeamStatus(classroomId = classroomId, courseId = courseId, assignmentId = assignmentId, teamId = team.createTeam.teamId, updateCreateTeamStatus = updateRequest)) {
            is Either.Right -> {
                getTeams(classroomId = classroomId, courseId = courseId, assignmentId = assignmentId)
            }
            is Either.Left -> { _errorClassCode = res.value }
        }
    }

    fun createTeamCompositeRejected(team: CreateTeamComposite, classroomId: Int, courseId: Int, assignmentId: Int) = viewModelScope.launch {
        val updateRequest = UpdateCreateTeamStatusInput(
            composite = UpdateCompositeState(requestId = team.createTeam.composite),
            createTeam = UpdateCreateTeamRequestState(requestId = team.createTeam.requestId, state = "Rejected", gitHubTeamId = null),
            createRepo = UpdateCreateRepoState(requestId = team.createTeam.requestId, state = "Rejected", url = null, repoId = team.createRepo.repoId),
            joinTeam = UpdateJoinTeamState(requestId = team.createTeam.requestId, state = "Rejected", userId = team.joinTeam.creator),
        )
        when (val res = classroomServices.changeCreateTeamStatus(classroomId = classroomId, courseId = courseId, assignmentId = assignmentId, teamId = team.createTeam.teamId, updateCreateTeamStatus = updateRequest)) {
            is Either.Right -> {
                getTeams(classroomId = classroomId, courseId = courseId, assignmentId = assignmentId)
            }
            is Either.Left -> { _errorClassCode = res.value }
        }
    }
}

private fun getState(res: Boolean?): String =
    when (res) {
        null, true -> "Accepted"
        false -> "Pending"
    }
