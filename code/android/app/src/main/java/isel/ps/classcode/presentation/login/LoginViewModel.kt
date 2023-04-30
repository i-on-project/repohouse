package isel.ps.classcode.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.presentation.login.services.LoginServices
import isel.ps.classcode.presentation.login.services.RequestInfo
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.launch
import java.util.UUID

class LoginViewModel(private val loginServices: LoginServices) : ViewModel() {

    private var _finished by mutableStateOf(false)
    val finished: Boolean
        get() = _finished

    private var _requestInfo: RequestInfo? by mutableStateOf(null)
    val requestInfo: RequestInfo?
        get() = _requestInfo

    fun auth() = viewModelScope.launch {
        val res = loginServices.auth()
        if (res is Either.Right) {
            _requestInfo = res.value
        }
        // TODO(): Handle error
    }
    fun tradeAndStoreAccessToken(code: String, state: String) = viewModelScope.launch {
        val requestInfo = requestInfo ?: TODO("Handle error")
        if (loginServices.tradeAndStoreAccessToken(code = code, stateCookie = requestInfo.stateCookie, state = state) is Either.Right) {
            _finished = true
        }
        // TODO(): Handle error
    }
}