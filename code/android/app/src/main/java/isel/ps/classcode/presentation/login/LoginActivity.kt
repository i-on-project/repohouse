package isel.ps.classcode.presentation.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsIntent
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
                        vm.startOAuth(startActivity = ::startActivity)
                    },
                )
                if (vm.finished) {
                    MenuActivity.navigate(origin = this)
                }
            }
        }
    }

    fun startActivity(uri: String, challenge: String): Boolean {
        return try {
            val url = Uri.parse(uri).buildUpon().apply {
                appendQueryParameter("challenge", challenge)
                appendQueryParameter("challengeMethod", "s256")
            }.build()
            val customIntent = CustomTabsIntent.Builder().build().intent.apply {
                data = url
            }
            startActivity(customIntent)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val code = intent?.data?.getQueryParameter("code")
        val state = intent?.data?.getQueryParameter("state")
        if (code != null && state != null) {
            vm.getAccessToken(code = code, state = state)
        }
    }
}
