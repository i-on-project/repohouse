package isel.ps.classcode.presentation.bootUp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.presentation.bootUp.services.BootUpServices
import kotlinx.coroutines.launch

class BootUpViewModel(private val sessionStore: SessionStore, private val bootUpServices: BootUpServices) : ViewModel() {
    private var _tokensExists by mutableStateOf(false)
    val tokensExists: Boolean
        get() = _tokensExists
    fun checkIfTokenExists() =
        viewModelScope.launch {
            _tokensExists = sessionStore.checkIfTokensExists()
        }

    fun getHome() =
        viewModelScope.launch {
            bootUpServices.getHome()
        }
}