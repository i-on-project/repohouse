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
import isel.ps.classcode.dataAccess.gitHubService.GitHubService
import isel.ps.classcode.domain.dto.LocalClassroomDto
import isel.ps.classcode.presentation.classroom.services.ClassroomServices
import isel.ps.classcode.presentation.team.TeamActivity
import isel.ps.classcode.ui.theme.ClasscodeTheme

class ClassroomActivity : ComponentActivity() {
    private val classroomServices: ClassroomServices by lazy { (application as DependenciesContainer).classroomServices }
    private val gitHubService: GitHubService by lazy { (application as DependenciesContainer).gitHubService }
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
                return ClassroomViewModel(classroomServices = classroomServices, gitHubService = gitHubService) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extra = getClassroomExtra() ?: return finish()
        vm.classroomInfo = extra
        vm.getAssignments()
        setContent {
            ClasscodeTheme {
                ClassroomScreen(
                    classroom = vm.classroomInfo.classroom,
                    teamsCreated = vm.teamsCreated,
                    assignments = vm.assignments,
                    assignment = vm.assignment,
                    onTeamSelected = { TeamActivity.navigate(origin = this, team = it.toLocalTeamDto(courseId = vm.classroomInfo.classroom.courseId, courseName = vm.classroomInfo.courseName, classroomId = vm.classroomInfo.classroom.id)) },
                    createTeamComposite = vm.createTeamComposite,
                    onAssignmentChange = { assignment -> vm.getTeams(assignmentId = assignment.id) },
                    onBackRequest = { finish() },
                    errorClassCode = vm.errorClassCode,
                    errorGitHub = vm.errorGitHub,
                    onDismissRequest = { finish() },
                    onCreateTeamComposite = { createTeamComposite, wasAccepted, assignment ->
                        if (wasAccepted) {
                            vm.createTeamCompositeAccepted(team = createTeamComposite, assignmentId = assignment.id)
                        } else {
                            vm.createTeamCompositeRejected(team = createTeamComposite, assignmentId = assignment.id)
                        }
                    },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val assignment = vm.assignment
        if (assignment != null) {
            vm.getTeams(assignmentId = assignment.id)
        }
    }

    @Suppress("deprecation")
    private fun getClassroomExtra(): ClassroomAndMoreInfo? {
        val classroomExtra: LocalClassroomDto? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(CLASSROOM_EXTRA, LocalClassroomDto::class.java)
            } else {
                intent.getParcelableExtra(CLASSROOM_EXTRA)
            }
        return if (classroomExtra == null) {
            null
        } else {
            ClassroomAndMoreInfo(classroom = classroomExtra.toClassroom(), courseName = classroomExtra.courseName)
        }
    }
}
