package isel.ps.classcode.presentation.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import isel.ps.classcode.AUTH_ENDPOINT
import isel.ps.classcode.BuildConfig.CLIENT_ID
import isel.ps.classcode.DependenciesContainer
import isel.ps.classcode.GITHUB_BASE_URL
import isel.ps.classcode.R
import isel.ps.classcode.SCOPE
import isel.ps.classcode.TAG
import isel.ps.classcode.presentation.login.services.LoginServices
import isel.ps.classcode.presentation.menu.MenuActivity
import java.util.UUID

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
                return LoginViewModel(githubLoginServices = githubLoginServices) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GithubLoginScreen(
                loginHandler = {
                    openGitHubLoginPage(state = UUID.randomUUID().toString())
               },
            )
            if (vm.finished) {
                MenuActivity.navigate(origin = this)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri = intent?.data
        val code = uri?.getQueryParameter("code")
        if (code != null) {
            vm.tradeAndStoreAccessToken(code = code)
        }
    }

    private fun openGitHubLoginPage(state: String) {
        try {
            val uri = Uri.parse("$GITHUB_BASE_URL$AUTH_ENDPOINT")
                .buildUpon()
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("scope", SCOPE)
                .appendQueryParameter("state", state)
                .build()

            val intent = Intent(Intent.ACTION_VIEW, uri)
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


