package isel.ps.classcode.presentation.classroom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.Team
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
    fun getAssignments(classroomId: Int, courseId: Int) = viewModelScope.launch {
        val assignments = classroomServices.getAssignments(classroomId = classroomId, courseId = courseId)
        if (assignments is Either.Right) {
            if (assignments.value.isNotEmpty()) {
                _assignments = assignments.value
                getTeams(classroomId = classroomId, courseId = courseId, assignmentId = assignments.value.first().id)
            } else {
                _assignments = emptyList()
                _teams = emptyList()
            }
        } else {
            // TODO(): Handle error
        }
    }

    fun getTeams(classroomId: Int, courseId: Int, assignmentId: Int) = viewModelScope.launch {
        val teams = classroomServices.getTeams(classroomId = classroomId, courseId = courseId, assignmentId = assignmentId)
        if (teams is Either.Right) {
            _teams = teams.value
        } else {
            // TODO(): Handle error
        }
    }
}