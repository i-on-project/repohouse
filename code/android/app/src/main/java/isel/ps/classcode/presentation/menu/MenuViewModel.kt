package isel.ps.classcode.presentation.menu

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.domain.Course
import isel.ps.classcode.domain.UserInfo
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.login.LoginActivity
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

    private var _errorClassCode: HandleClassCodeResponseError? by mutableStateOf(null)
    val errorClassCode: HandleClassCodeResponseError?
        get() = _errorClassCode

    private var _errorGitHub: HandleGitHubResponseError? by mutableStateOf(null)
    val errorGitHub: HandleGitHubResponseError?
        get() = _errorGitHub

    fun getUserInfo() = viewModelScope.launch {
        when(val userInfo = menuServices.getUserInfo()) {
            is Either.Right -> { _userInfo = userInfo.value }
            is Either.Left -> { _errorGitHub = userInfo.value }
        }
    }

    fun getCourses() = viewModelScope.launch {
        when(val courses = menuServices.getCourses()) {
            is Either.Right -> { _courses = courses.value }
            is Either.Left -> { _errorClassCode = courses.value }
        }
    }

    fun logout() = viewModelScope.launch {
        menuServices.logout()
    }
}