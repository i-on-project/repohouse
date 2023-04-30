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
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import isel.ps.classcode.DependenciesContainer
import isel.ps.classcode.R
import isel.ps.classcode.TAG
import isel.ps.classcode.presentation.login.services.LoginServices
import isel.ps.classcode.presentation.menu.MenuActivity
import isel.ps.classcode.ui.theme.ClasscodeTheme


class LoginActivity : ComponentActivity() {
    private val githubLoginServices: LoginServices by lazy { (application as DependenciesContainer).loginServices }

    companion object {
        fun navigate(origin: ComponentActivity) {
            with(origin) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val vm by viewModels<LoginViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(loginServices = githubLoginServices) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClasscodeTheme {
                LoginScreen(
                    loginHandler = {
                        vm.auth()
                    },
                )
                val requestInfo = vm.requestInfo
                if (requestInfo != null) {
                    openGitHubLoginPage(url = requestInfo.url)
                }
                if (vm.finished) {
                    MenuActivity.navigate(origin = this)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri = intent?.data

    }

    private fun openGitHubLoginPage(url: String) {
        try {
            val uri = Uri.parse(url)
            val builder = CustomTabsIntent.Builder()
            val intent = builder.build().intent.apply {
                putExtra("com.google.android.apps.chrome.EXTRA_OPEN_NEW_INCOGNITO_TAB", true)
                data = uri
            }
            startActivity(intent)
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


