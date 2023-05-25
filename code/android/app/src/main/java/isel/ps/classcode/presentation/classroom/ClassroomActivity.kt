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
import isel.ps.classcode.presentation.classroom.services.ClassroomServices
import isel.ps.classcode.presentation.team.TeamActivity
import isel.ps.classcode.presentation.views.LoadingAnimationCircle
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
        val pair = getClassroomExtra()
        val classroom = pair.first ?: return finish()
        val courseName = pair.second ?: return finish()
        vm.getAssignments(classroomId =  classroom.id, courseId = classroom.courseId)
        setContent {
            val assignments = vm.assignments
            ClasscodeTheme {
                ClassroomScreen(
                    classroom = classroom,
                    teamsCreated = vm.teamsCreated,
                    assignments = assignments,
                    assignment = vm.assignment,
                    onTeamSelected = { TeamActivity.navigate(origin = this, team = it.toLocalTeamDto(courseId = classroom.courseId, courseName = courseName, classroomId = classroom.id))},
                    createTeamComposite = vm.createTeamComposite,
                    onAssignmentChange = { assignment -> vm.getTeams(courseId = classroom.courseId, classroomId = classroom.id, assignmentId = assignment.id) },
                    onBackRequest = { finish() },
                    error = vm.errorClassCode,
                    onDismissRequest = { finish() },
                    onCreateTeamComposite = { createTeamComposite, wasAccepted, assignment ->
                        if (wasAccepted) {
                            vm.createTeamCompositeAccepted(team = createTeamComposite, courseName = courseName, courseId = classroom.courseId, classroomId = classroom.id, assignmentId = assignment.id)
                        } else {
                            vm.createTeamCompositeRejected(team = createTeamComposite, classroomId = classroom.id, assignmentId = assignment.id, courseId = classroom.courseId)
                        }
                    }
                )
            }
        }
    }

    @Suppress("deprecation")
    private fun getClassroomExtra(): Pair<Classroom?, String?> {
        val classroomExtra: LocalClassroomDto? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra(CLASSROOM_EXTRA, LocalClassroomDto::class.java)
            else
                intent.getParcelableExtra(CLASSROOM_EXTRA)
        return Pair(classroomExtra?.toClassroom(), classroomExtra?.courseName)
    }
}