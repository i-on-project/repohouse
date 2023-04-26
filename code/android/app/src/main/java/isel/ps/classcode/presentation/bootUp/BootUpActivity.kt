package isel.ps.classcode.presentation.bootUp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import isel.ps.classcode.DependenciesContainer
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.presentation.login.LoginActivity
import isel.ps.classcode.presentation.menu.MenuActivity
import isel.ps.classcode.ui.theme.ClasscodeTheme

class BootUpActivity : ComponentActivity() {

    private val sessionStore: SessionStore by lazy { (application as DependenciesContainer).sessionStore }

    companion object {
        fun navigate(origin: Activity) {
            with(origin) {
                val intent = Intent(this, BootUpActivity::class.java)
                startActivity(intent)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val vm by viewModels<BootUpViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BootUpViewModel(sessionStore = sessionStore) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.checkIfTokenExists()
        setContent {
            ClasscodeTheme {
                BootUpScreen(
                    actionHandler = {
                        if (vm.tokenExists) {
                            MenuActivity.navigate(origin = this)
                        } else {
                            LoginActivity.navigate(origin = this)
                        }
                    }
                )
            }
        }
    }
}