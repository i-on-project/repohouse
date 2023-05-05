package isel.ps.classcode.presentation.classroom

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import isel.ps.classcode.DependenciesContainer
import isel.ps.classcode.domain.Classroom
import isel.ps.classcode.domain.dto.LocalClassroomDto
import isel.ps.classcode.domain.dto.LocalCourseDto
import isel.ps.classcode.presentation.classroom.services.ClassroomServices
import isel.ps.classcode.ui.theme.ClasscodeTheme

class ClassroomActivity : ComponentActivity() {
    private val classroomServices: ClassroomServices by lazy { (application as DependenciesContainer).classroomServices }

    companion object {
        const val CLASSROOM_EXTRA = "CLASSROOM_EXTRA"
        fun navigate(origin: Activity, classroom: LocalClassroomDto) {
            with(origin) {
                val intent = Intent(this, ClassroomActivity::class.java)
                intent.putExtra(CLASSROOM_EXTRA, classroom)
                startActivity(intent)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val vm by viewModels<ClassroomViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ClassroomViewModel(classroomServices = classroomServices) as T
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val classroom = getClassroomExtra()
        if (classroom != null) {
            vm.getClassroom(classroom.id)
        }
        setContent {
            ClasscodeTheme {
                if (classroom != null)
                    ClassroomScreen(
                        classroom = classroom,
                        teams = vm.teams,
                        onTeamSelected = {  },
                        onBackRequest = { finish() }
                    )
            }
        }
    }

    @Suppress("deprecation")
    private fun getClassroomExtra(): Classroom? {
        val classroomExtra: LocalClassroomDto? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra(CLASSROOM_EXTRA, LocalClassroomDto::class.java)
            else
                intent.getParcelableExtra(CLASSROOM_EXTRA)
        return if (classroomExtra != null) Classroom(
            id = classroomExtra.id,
            lastSync = classroomExtra.lastSync,
            name = classroomExtra.name,
            inviteLink = classroomExtra.inviteLink,
            isArchived = classroomExtra.isArchived,
        ) else null
    }
}