package isel.ps.classcode.presentation.classroom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.CreateTeamComposite
import isel.ps.classcode.domain.Team
import isel.ps.classcode.domain.TeamNotCreated
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.classroom.services.ClassroomServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.launch

class ClassroomViewModel(private val classroomServices: ClassroomServices) : ViewModel() {

    var courseName: String? by mutableStateOf("")
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
        when(val assignments = classroomServices.getAssignments(classroomId = classroomId, courseId = courseId)) {
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
        when(val teams = classroomServices.getTeams(classroomId = classroomId, courseId = courseId, assignmentId = assignmentId)) {
            is Either.Right -> {
                _teamsCreated = teams.value.teamsCreated
                _createTeamComposite = teams.value.createTeamComposite
            }
            is Either.Left -> { _errorClassCode = teams.value }
        }
    }

    fun createTeamAccepted(team: TeamNotCreated, classroomId: Int, courseId: Int, assignmentId: Int) = viewModelScope.launch {
        when(val result = classroomServices.createTeamInGitHub(orgName = courseName!!, teamName = team.name)) {
            is Either.Right -> {
                when(val res = classroomServices.changeCreateTeamStatus(classroomId = classroomId, courseId = courseId, assignmentId = assignmentId, teamId = team.id, state = "Accepted")) {
                    is Either.Right -> {
                        getTeams(classroomId = classroomId, courseId = courseId, assignmentId = assignmentId)
                    }
                    is Either.Left -> { _errorClassCode = res.value }
                }
            }
            is Either.Left -> { _errorGitHub = result.value }
        }
    }
}