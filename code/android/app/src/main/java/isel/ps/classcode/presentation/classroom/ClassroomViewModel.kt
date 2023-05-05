package isel.ps.classcode.presentation.classroom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.domain.Team
import isel.ps.classcode.presentation.classroom.services.ClassroomServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.launch

class ClassroomViewModel(private val classroomServices: ClassroomServices) : ViewModel() {

    val teams: List<Team>?
        get() = _teams

    private var _teams by mutableStateOf<List<Team>?>(null)
    fun getClassroom(classroomId: Int) = viewModelScope.launch {
        val teams = classroomServices.getTeams(classroomId = classroomId)
        if (teams is Either.Right) {
            _teams = teams.value
        } else {
            // TODO(): Handle error
        }
    }
}