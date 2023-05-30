package isel.ps.classcode.presentation.menu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import isel.ps.classcode.DependenciesContainer
import isel.ps.classcode.dataAccess.gitHubFunctions.GitHubFunctions
import isel.ps.classcode.presentation.course.CourseActivity
import isel.ps.classcode.presentation.credits.CreditsActivity
import isel.ps.classcode.presentation.login.LoginActivity
import isel.ps.classcode.presentation.menu.services.MenuServices
import isel.ps.classcode.ui.theme.ClasscodeTheme

class MenuActivity : ComponentActivity() {
    private val menuServices: MenuServices by lazy { (application as DependenciesContainer).menuServices }
    private val gitHubFunctions: GitHubFunctions by lazy { (application as DependenciesContainer).gitHubFunctions }

    companion object {
        fun navigate(origin: Activity) {
            with(origin) {
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val vm by viewModels<MenuViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MenuViewModel(menuServices = menuServices, gitHubFunctions = gitHubFunctions) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.getUserInfo()
        vm.getCourses()
        setContent {
            ClasscodeTheme {
                MenuScreen(
                    userInfo = vm.userInfo,
                    courses = vm.courses,
                    onBackRequest = {
                        vm.logout()
                        LoginActivity.navigate(origin = this, flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    },
                    onCreditsRequested = { CreditsActivity.navigate(origin = this) },
                    onCourseSelected = { course ->
                        CourseActivity.navigate(origin = this, course = course.toLocalCourseDto())
                    },
                    errorGitHub = vm.errorGitHub,
                    errorClassCode = vm.errorClassCode,
                    onDismissRequest = { finish() }
                )
            }
        }
    }
}
