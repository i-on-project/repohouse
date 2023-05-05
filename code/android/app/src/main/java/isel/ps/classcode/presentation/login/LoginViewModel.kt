package isel.ps.classcode.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.presentation.login.services.LoginServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.launch

class LoginViewModel(private val loginServices: LoginServices) : ViewModel() {

    private var _finished by mutableStateOf(false)
    val finished: Boolean
        get() = _finished

    fun getAccessToken(code: String, githubId: String) = viewModelScope.launch {
        val res = loginServices.getTheTokens(code = code, githubId = githubId)
        if (res is Either.Right) {
            val x = 0
        }
        // TODO(): Handle error
    }
}