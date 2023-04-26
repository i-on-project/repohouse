package isel.ps.classcode.presentation.bootUp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import kotlinx.coroutines.launch

class BootUpViewModel(private val sessionStore: SessionStore) : ViewModel() {
    private var _tokenExists by mutableStateOf(false)
    val tokenExists: Boolean
        get() = _tokenExists
    fun checkIfTokenExists() =
        viewModelScope.launch {
            _tokenExists = sessionStore.checkIfTokenExists()
        }
}