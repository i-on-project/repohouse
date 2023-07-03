package isel.ps.classcode.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.login.services.LoginServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.launch

class LoginViewModel(private val loginServices: LoginServices) : ViewModel() {

    private var _finished by mutableStateOf(false)
    val finished: Boolean
        get() = _finished

    private var _error: HandleClassCodeResponseError? by mutableStateOf(null)
    val error: HandleClassCodeResponseError?
        get() = _error

    fun getAccessToken(code: String, state: String) = viewModelScope.launch {
        when (val res = loginServices.getTheAccessToken(code = code, state = state)) {
            is Either.Right -> { _finished = true }
            is Either.Left -> { _error = res.value }
        }
    }

    fun startOAuth(startActivity: (String, String) -> Boolean) = viewModelScope.launch {
        loginServices.startOauth(startActivity = startActivity)
    }

    fun dismissError() {
        _error = null
    }
}
