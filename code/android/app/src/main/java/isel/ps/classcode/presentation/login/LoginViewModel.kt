package isel.ps.classcode.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.presentation.login.services.LoginServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.launch

class LoginViewModel(private val githubLoginServices: LoginServices) : ViewModel() {

    private var _finished by mutableStateOf(false)
    val finished: Boolean
        get() = _finished

    fun tradeAndStoreAccessToken(code: String) = viewModelScope.launch {
        if (githubLoginServices.tradeAndStoreAccessToken(code = code) is Either.Right) {
            _finished = true
        }
        // TODO(): Handle error
    }
}