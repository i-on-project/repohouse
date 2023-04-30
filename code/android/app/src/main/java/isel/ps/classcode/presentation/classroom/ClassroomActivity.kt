package isel.ps.classcode.presentation.classroom

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import isel.ps.classcode.DependenciesContainer
import isel.ps.classcode.presentation.classroom.services.ClassroomServices
import isel.ps.classcode.ui.theme.ClasscodeTheme

class ClassroomActivity : ComponentActivity() {
    private val classroomServices: ClassroomServices by lazy { (application as DependenciesContainer).classroomServices }

    @Suppress("UNCHECKED_CAST")
    private val vm by viewModels<ClassroomViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ClassroomViewModel() as T
            }
        }
    }

    companion object {
        fun navigate(origin: Activity) {
            with(origin) {
                val intent = Intent(this, ClassroomActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClasscodeTheme {

            }
        }
    }
}