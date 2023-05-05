package isel.ps.classcode.presentation.menu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.domain.Course
import isel.ps.classcode.domain.UserInfo
import isel.ps.classcode.presentation.menu.services.MenuServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.launch

class MenuViewModel(private val menuServices: MenuServices) : ViewModel() {
    val userInfo: UserInfo?
        get() = _userInfo
    private var _userInfo by mutableStateOf<UserInfo?>(null)

    val courses: List<Course>?
        get() = _courses
    private var _courses by mutableStateOf<List<Course>?>(null)

    fun getUserInfo() = viewModelScope.launch {
        val userInfo = menuServices.getUserInfo()
        if(userInfo is Either.Right) {
            _userInfo = userInfo.value
        }
        // TODO(): Handle error
    }

    fun getCourses() = viewModelScope.launch {
        val courses = menuServices.getCourses()
        if(courses is Either.Right) {
            _courses = courses.value
        }
        // TODO(): Handle error
    }

    fun logout() = menuServices.logout()
}