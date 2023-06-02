package isel.ps.classcode.presentation.classroom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.dataAccess.gitHubService.GitHubService
import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.CreateTeamComposite
import isel.ps.classcode.domain.Team
import isel.ps.classcode.domain.UpdateArchiveRepoInput
import isel.ps.classcode.domain.UpdateArchiveRepoState
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

class ClassroomViewModel(private val classroomServices: ClassroomServices, private val gitHubService: GitHubService) : ViewModel() {
    lateinit var classroomInfo: ClassroomAndMoreInfo
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

    fun getAssignments() = viewModelScope.launch {
        when (val assignments = classroomServices.getAssignments(classroomId = classroomInfo.classroom.id, courseId = classroomInfo.classroom.courseId)) {
            is Either.Right -> {
                val archiveRepos = assignments.value.archiveRepos
                val list = mutableListOf<UpdateArchiveRepoState>()
                _assignments = assignments.value.assignments
                if (archiveRepos?.isNotEmpty() == true) {
                    archiveRepos.forEach { request ->
                        when (gitHubService.archiveRepoInGithub(orgName = classroomInfo.courseName, repoName = request.repoName)) {
                            is Either.Right -> {
                                list.add(UpdateArchiveRepoState(requestId = request.requestId, state = "Accepted"))
                            }
                            is Either.Left -> {
                                list.add(UpdateArchiveRepoState(requestId = request.requestId, state = "Pending"))
                            }
                        }
                    }
                    val updateArchiveRepo = UpdateArchiveRepoInput(archiveRepos = list, composite = UpdateCompositeState(requestId = archiveRepos.first().composite))
                    when (val result = classroomServices.changeStatusArchiveRepoInClassCode(courseId = classroomInfo.classroom.courseId, classroomId = classroomInfo.classroom.id, updateArchiveRepo = updateArchiveRepo)) {
                        is Either.Right -> Unit // Do nothing

                        is Either.Left -> _errorClassCode = result.value
                    }
                }

                if (assignments.value.assignments.isNotEmpty()) {
                    _assignment = assignments.value.assignments.first()
                    getTeams(assignmentId = assignments.value.assignments.first().id)
                } else {
                    _assignment = null
                    _teamsCreated = emptyList()
                    _createTeamComposite = emptyList()
                }
            }
            is Either.Left -> {
                _errorClassCode = assignments.value
            }
        }
    }

    fun getTeams(assignmentId: Int) = viewModelScope.launch {
        val x = classroomInfo.classroom.id
        val y = classroomInfo.classroom.courseId
        val z = assignmentId
        when (val teams = classroomServices.getTeams(classroomId = x, courseId = y, assignmentId = z)) {
            is Either.Right -> {
                _teamsCreated = teams.value.teamsCreated
                _createTeamComposite = teams.value.createTeamComposite
            }
            is Either.Left -> { _errorClassCode = teams.value }
        }
    }

    fun createTeamCompositeAccepted(team: CreateTeamComposite, assignmentId: Int) = viewModelScope.launch {
        val teamSlug = team.createTeam.teamName.replace(" ", "-")
        val createTeamResult = if (team.createTeam.gitHubTeamId != null) null else gitHubService.createTeamInGitHub(createTeamComposite = team, orgName = classroomInfo.courseName)
        val githubTeamId = when (createTeamResult) {
            is Either.Right -> createTeamResult.value
            is Either.Left -> {
                _errorGitHub = createTeamResult.value
                return@launch
            }
            else -> team.createTeam.gitHubTeamId
        }
        val createRepoResult = if (team.createRepo.state == "Accepted") null else gitHubService.createRepoInGitHub(orgName = classroomInfo.courseName, repo = team.createRepo, teamId = githubTeamId)
        val createRepo = when (createRepoResult) {
            is Either.Right -> createRepoResult.value
            is Either.Left -> {
                _errorGitHub = createRepoResult.value
                return@launch
            }
            else -> null
        }
        val joinTeamResult = if (team.joinTeam.state == "Accepted") null else gitHubService.addMemberToTeamInGitHub(orgName = classroomInfo.courseName, teamSlug = teamSlug, username = team.joinTeam.githubUsername)
        when (joinTeamResult) {
            is Either.Left -> {
                _errorGitHub = joinTeamResult.value
                return@launch
            }
            else -> {} // DO NOTHING
        }
        val updateRequest = UpdateCreateTeamStatusInput(
            composite = UpdateCompositeState(requestId = team.createTeam.composite),
            createTeam = UpdateCreateTeamRequestState(requestId = team.createTeam.requestId, state = getState(res = createTeamResult), gitHubTeamId = githubTeamId),
            createRepo = UpdateCreateRepoState(requestId = team.createRepo.requestId, state = getState(res = createRepoResult), url = createRepo, repoId = team.createRepo.repoId),
            joinTeam = UpdateJoinTeamState(requestId = team.joinTeam.requestId, state = getState(res = joinTeamResult), userId = team.joinTeam.creator),
        )
        when (val res = classroomServices.changeCreateTeamStatus(classroomId = classroomInfo.classroom.id, courseId = classroomInfo.classroom.courseId, assignmentId = assignmentId, teamId = team.createTeam.teamId, updateCreateTeamStatus = updateRequest)) {
            is Either.Right -> {
                val teamsCreated = _teamsCreated
                val createTeamComposite = _createTeamComposite
                if (teamsCreated != null && createTeamComposite != null) {
                    val newTeam = Team(id = team.createTeam.teamId, name = team.createTeam.teamName, isCreated = true, isClosed = true, assignment = assignmentId)
                    _teamsCreated = teamsCreated + newTeam
                    _createTeamComposite = createTeamComposite.filter { it.createTeam.teamId != team.createTeam.teamId }
                }
            }
            is Either.Left -> _errorClassCode = res.value
        }
    }

    fun createTeamCompositeRejected(team: CreateTeamComposite, assignmentId: Int) = viewModelScope.launch {
        val updateRequest = UpdateCreateTeamStatusInput(
            composite = UpdateCompositeState(requestId = team.createTeam.composite),
            createTeam = UpdateCreateTeamRequestState(requestId = team.createTeam.requestId, state = "Rejected", gitHubTeamId = null),
            createRepo = UpdateCreateRepoState(requestId = team.createTeam.requestId, state = "Rejected", url = null, repoId = team.createRepo.repoId),
            joinTeam = UpdateJoinTeamState(requestId = team.createTeam.requestId, state = "Rejected", userId = team.joinTeam.creator),
        )
        when (val res = classroomServices.changeCreateTeamStatus(classroomId = classroomInfo.classroom.id, courseId = classroomInfo.classroom.courseId, assignmentId = assignmentId, teamId = team.createTeam.teamId, updateCreateTeamStatus = updateRequest)) {
            is Either.Right -> {
                val teamsCreated = _teamsCreated
                val createTeamComposite = _createTeamComposite
                if (teamsCreated != null && createTeamComposite != null) {
                    _createTeamComposite = createTeamComposite.filter { it.createTeam.teamId != team.createTeam.teamId } + team.copy(compositeState = "Rejected ", createTeam = team.createTeam.copy(state = "Rejected"), joinTeam = team.joinTeam.copy(state = "Rejected"), createRepo = team.createRepo.copy(state = "Rejected"))
                }
            }
            is Either.Left -> _errorClassCode = res.value
        }
    }
}

private fun <T>getState(res: Either<HandleGitHubResponseError, T>?): String =
    when (res) {
        is Either.Right, null -> "Accepted"
        is Either.Left -> "Pending"
    }
