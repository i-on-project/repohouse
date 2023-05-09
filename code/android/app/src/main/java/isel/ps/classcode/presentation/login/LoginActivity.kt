package isel.ps.classcode.presentation.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import isel.ps.classcode.CLASSCODE_AUTH_URL
import isel.ps.classcode.DependenciesContainer
import isel.ps.classcode.R
import isel.ps.classcode.TAG
import isel.ps.classcode.presentation.connectivityObserver.NetworkConnectivityObserver
import isel.ps.classcode.presentation.login.services.LoginServices
import isel.ps.classcode.presentation.menu.MenuActivity
import isel.ps.classcode.ui.theme.ClasscodeTheme


class LoginActivity : ComponentActivity() {
    private val githubLoginServices: LoginServices by lazy { (application as DependenciesContainer).loginServices }

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
                return LoginViewModel(loginServices = githubLoginServices, connectivityObserver = NetworkConnectivityObserver(context = this@LoginActivity)) as T
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
                        sendToClassscodeToStartOauthScheme()
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
        val githubId = intent?.data?.getQueryParameter("github_id")
        if (code != null && githubId != null) {
            vm.getAccessToken(code, githubId)
        }
    }



    private fun sendToClassscodeToStartOauthScheme() {
        try {
            val uri = Uri.parse(CLASSCODE_AUTH_URL)
            val customIntent = CustomTabsIntent.Builder().build().intent.apply {
                data = uri
            }
            startActivity(customIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open URL", e)
            Toast
                .makeText(
                    this,
                    R.string.failed_url_open,
                    Toast.LENGTH_LONG
                )
                .show()
        }
    }
}


