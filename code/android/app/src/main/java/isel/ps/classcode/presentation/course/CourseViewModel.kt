package isel.ps.classcode.presentation.course

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.domain.Classroom
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.course.services.CourseServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.launch

class CourseViewModel(private val courseServices: CourseServices) : ViewModel() {
    val classrooms: List<Classroom>?
        get() = _classrooms
    private var _classrooms by mutableStateOf<List<Classroom>?>(null)

    private var _error: HandleClassCodeResponseError? by mutableStateOf(null)
    val error: HandleClassCodeResponseError?
        get() = _error
    fun getClassrooms(courseId: Int) {
        viewModelScope.launch {
            when(val classrooms = courseServices.getClassrooms(courseId = courseId)) {
                is Either.Right -> { _classrooms = classrooms.value }
                is Either.Left -> { _error = classrooms.value }
            }
        }
    }
}