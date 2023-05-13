package isel.ps.classcode.presentation.login

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.connectivityObserver.ConnectivityObserver
import isel.ps.classcode.presentation.login.services.LoginServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

class LoginViewModel(private val loginServices: LoginServices, private val connectivityObserver: ConnectivityObserver) : ViewModel() {

    private var _finished by mutableStateOf(false)
    val finished: Boolean
        get() = _finished

    private var _error: HandleClassCodeResponseError? by mutableStateOf(null)
    val error: HandleClassCodeResponseError?
        get() = _error

    lateinit var status: ConnectivityObserver

    fun getAccessToken(code: String, state: String) = viewModelScope.launch {
        status = connectivityObserver
        status.observer().onEach {  }
        when (val res = loginServices.getTheAccessToken(code = code, state = state)) {
            is Either.Right -> { _finished = true }
            is Either.Left -> { _error = res.value }
        }
    }

    fun startOAuth(activity: Activity) = viewModelScope.launch {
        loginServices.startOauth(activity = activity)
    }

}