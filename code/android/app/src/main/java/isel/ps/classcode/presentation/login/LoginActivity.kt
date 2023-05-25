package isel.ps.classcode.presentation.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import isel.ps.classcode.DependenciesContainer
import isel.ps.classcode.presentation.connectivityObserver.NetworkConnectivityObserver
import isel.ps.classcode.presentation.login.services.LoginServices
import isel.ps.classcode.presentation.menu.MenuActivity
import isel.ps.classcode.ui.theme.ClasscodeTheme

class LoginActivity : ComponentActivity() {
    private val loginServices: LoginServices by lazy { (application as DependenciesContainer).loginServices }
    companion object {
        fun navigate(origin: ComponentActivity, flags: Int? = null) {
            with(origin) {
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags?.let { addFlags(it) }
                }
                startActivity(intent)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val vm by viewModels<LoginViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(loginServices = loginServices, connectivityObserver = NetworkConnectivityObserver(context = this@LoginActivity)) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ClasscodeTheme {
                LoginScreen(
                    error = vm.error,
                    onDismissRequest = { finish() },
                    loginHandler = {
                        vm.startOAuth(activity = this)
                    },
                )
                if (vm.finished) {
                    MenuActivity.navigate(origin = this)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val code = intent?.data?.getQueryParameter("code")
        val state = intent?.data?.getQueryParameter("state")
        if (code != null && state != null) {
            vm.getAccessToken(code = code, state = state)
        }
    }
}
