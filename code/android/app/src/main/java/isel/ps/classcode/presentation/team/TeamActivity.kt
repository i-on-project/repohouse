package isel.ps.classcode.presentation.team
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import isel.ps.classcode.DependenciesContainer
import isel.ps.classcode.domain.dto.LocalTeamDto
import isel.ps.classcode.dataAccess.gitHubService.GitHubService
import isel.ps.classcode.presentation.classroom.ClassroomActivity
import isel.ps.classcode.presentation.team.services.TeamServices
import isel.ps.classcode.ui.theme.ClasscodeTheme

class TeamActivity : ComponentActivity() {
    private val teamServices: TeamServices by lazy { (application as DependenciesContainer).teamServices }
    private val gitHubService: GitHubService by lazy { (application as DependenciesContainer).gitHubService }

    companion object {
        const val TEAM_EXTRA = "TEAM_EXTRA"
        fun navigate(origin: ComponentActivity, team: LocalTeamDto) {
            with(origin) {
                val intent = Intent(this, TeamActivity::class.java).apply {
                    putExtra(TEAM_EXTRA, team)
                }
                startActivity(intent)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val vm by viewModels<TeamViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TeamViewModel(teamServices = teamServices, gitHubService = gitHubService) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extra = getTeamExtra() ?: return finish()
        vm.teamInfo = extra
        vm.getTeamRequests()
        setContent {
            ClasscodeTheme {
                TeamScreen(
                    team = vm.teamInfo.team,
                    requests = vm.teamRequests,
                    onJoinTeamAccepted = { joinTeam -> vm.addMemberToTeam(joinTeam = joinTeam) },
                    onJoinTeamRejected = { joinTeam -> vm.rejectJoinTeamRequest(joinTeam = joinTeam) },
                    onLeaveTeamAccepted = { leaveTeam -> vm.removeMemberFromTeam(leaveTeam = leaveTeam, activity = this) },
                    onLeaveTeamRejected = { leaveTeam -> vm.rejectLeaveTeamRequest(leaveTeam = leaveTeam) },
                    errorClassCode = vm.errorClassCode,
                    errorGitHub = vm.errorGitHub,
                    onDismissRequest = { finish() },
                    onBackRequest = { finish() },
                    onReloadRequest = { vm.getTeamRequests() },
                )
            }
        }
    }

    @Suppress("deprecation")
    private fun getTeamExtra(): TeamAndOtherInfo? {
        val teamExtra: LocalTeamDto? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(TEAM_EXTRA, LocalTeamDto::class.java)
            } else {
                intent.getParcelableExtra(ClassroomActivity.CLASSROOM_EXTRA)
            }
        return if (teamExtra == null) {
            null
        } else {
            TeamAndOtherInfo(team = teamExtra.toTeam(), courseName = teamExtra.courseName, courseId = teamExtra.courseId, classroomId = teamExtra.classroomId)
        }
    }
}
