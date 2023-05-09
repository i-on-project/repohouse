package isel.ps.classcode.presentation.classroom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.Team
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.classroom.services.ClassroomServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.launch

class ClassroomViewModel(private val classroomServices: ClassroomServices) : ViewModel() {

    val assignments: List<Assignment>?
        get() = _assignments
    private var _assignments by mutableStateOf<List<Assignment>?>(null)

    val teams: List<Team>?
        get() = _teams
    private var _teams by mutableStateOf<List<Team>?>(null)

    private var _error: HandleClassCodeResponseError? by mutableStateOf(null)
    val error: HandleClassCodeResponseError?
        get() = _error

    fun getAssignments(classroomId: Int, courseId: Int) = viewModelScope.launch {
        when(val assignments = classroomServices.getAssignments(classroomId = classroomId, courseId = courseId)) {
            is Either.Right -> {
                if (assignments.value.isNotEmpty()) {
                    _assignments = assignments.value
                    getTeams(classroomId = classroomId, courseId = courseId, assignmentId = assignments.value.first().id)
                } else {
                    _assignments = emptyList()
                    _teams = emptyList()
                }
            }
            is Either.Left -> {
                _error = assignments.value
            }
        }
    }

    fun getTeams(classroomId: Int, courseId: Int, assignmentId: Int) = viewModelScope.launch {
        when(val teams = classroomServices.getTeams(classroomId = classroomId, courseId = courseId, assignmentId = assignmentId)) {
            is Either.Right -> {  _teams = teams.value }
            is Either.Left -> { _error = teams.value }
        }
    }
}