package isel.ps.classcode.presentation.course

import android.app.Activity
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import isel.ps.classcode.dataAccess.gitHubService.GitHubService
import isel.ps.classcode.domain.Classroom
import isel.ps.classcode.domain.Course
import isel.ps.classcode.domain.LeaveClassroomRequest
import isel.ps.classcode.domain.LeaveCourse
import isel.ps.classcode.domain.LeaveTeamWithDelete
import isel.ps.classcode.domain.UpdateCompositeState
import isel.ps.classcode.domain.UpdateLeaveClassroom
import isel.ps.classcode.domain.UpdateLeaveClassroomCompositeInput
import isel.ps.classcode.domain.UpdateLeaveCourse
import isel.ps.classcode.domain.UpdateLeaveCourseCompositeInput
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.course.services.CourseServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.launch

class CourseViewModel(private val courseServices: CourseServices, private val gitHubService: GitHubService) : ViewModel() {
    lateinit var course: Course
    val classrooms: List<Classroom>?
        get() = _classrooms
    private var _classrooms by mutableStateOf<List<Classroom>?>(null)

    private var _errorClassCode: HandleClassCodeResponseError? by mutableStateOf(null)
    val errorClassCode: HandleClassCodeResponseError?
        get() = _errorClassCode

    private var _errorGithub: HandleGitHubResponseError? by mutableStateOf(null)
    val errorGithub: HandleGitHubResponseError?
        get() = _errorGithub

    fun getClassrooms(activity: Activity) {
        viewModelScope.launch {
            when (val result = courseServices.getClassrooms(courseId = course.id)) {
                is Either.Right -> {
                    _classrooms = result.value.classrooms
                    if (result.value.leaveCourseRequests.isNotEmpty()) {
                        result.value.leaveCourseRequests.forEach { leaveCourseRequest ->
                            leaveCourseInGitHub(
                                leaveCourse = leaveCourseRequest.leaveCourse,
                                leaveClassroomRequests = leaveCourseRequest.leaveClassroomRequests,
                                activity = activity,
                            )
                        }
                    }
                }

                is Either.Left -> {
                    _errorClassCode = result.value
                }
            }
        }
    }

    private fun leaveCourseInGitHub(leaveCourse: LeaveCourse, leaveClassroomRequests: List<LeaveClassroomRequest>, activity: Activity) {
        viewModelScope.launch {
            when (val result = gitHubService.leaveCourseInGitHub(orgName = course.name, username = leaveCourse.githubUsername)) {
                is Either.Right -> {
                    val leaveClassrooms = leaveClassroomRequests.map { leaveClassroom ->
                        val list = leaveClassroom.leaveTeamRequests.map { leaveTeamWithRepoName ->
                            val leaveTeam = leaveTeamWithRepoName.leaveTeam
                            if (leaveTeam.membersCount == 1) {
                                when (gitHubService.deleteTeamFromTeamInGitHub(courseName = course.name, teamSlug = leaveTeam.teamName)) {
                                    is Either.Right -> {
                                        when (val x = gitHubService.archiveRepoInGithub(orgName = course.name, repoName = leaveTeamWithRepoName.repoName)) {
                                            is Either.Right -> Unit
                                            is Either.Left -> _errorGithub = x.value
                                        }
                                        LeaveTeamWithDelete(requestId = leaveTeam.requestId, teamId = leaveTeam.teamId, state = "Accepted", wasDeleted = true)
                                    }
                                    is Either.Left -> LeaveTeamWithDelete(requestId = leaveTeam.requestId, teamId = leaveTeam.teamId, state = leaveTeam.state)
                                }
                            } else {
                                when (gitHubService.removeMemberFromTeamInGitHub(leaveTeam = leaveTeamWithRepoName.leaveTeam, courseName = course.name, teamSlug = leaveTeamWithRepoName.leaveTeam.teamName,)) {
                                    is Either.Right -> LeaveTeamWithDelete(requestId = leaveTeam.requestId, teamId = leaveTeam.teamId, state = "Accepted")
                                    is Either.Left -> LeaveTeamWithDelete(requestId = leaveTeam.requestId, teamId = leaveTeam.teamId, state = leaveTeam.state)
                                }
                            }
                        }
                        UpdateLeaveClassroomCompositeInput(composite = UpdateCompositeState(requestId = leaveCourse.composite), leaveClassroom = UpdateLeaveClassroom(requestId = leaveClassroom.leaveClassroom.requestId, classroomId = leaveClassroom.leaveClassroom.classroomId), leaveTeams = list)
                    }
                    when (val res = courseServices.updateLeaveCourseCompositeInClassCode(input = UpdateLeaveCourseCompositeInput(composite = UpdateCompositeState(requestId = leaveCourse.composite), leaveCourse = UpdateLeaveCourse(requestId = leaveCourse.requestId, courseId = leaveCourse.courseId), leaveClassrooms = leaveClassrooms), userId = leaveCourse.creator)) {
                        is Either.Right -> {
                            Toast
                                .makeText(
                                    activity,
                                    "The user with id ${leaveCourse.creator} has left the course successfully",
                                    Toast.LENGTH_LONG,
                                )
                                .show()
                        }
                        is Either.Left -> {
                            _errorClassCode = res.value
                        }
                    }
                }
                is Either.Left -> {
                    _errorGithub = result.value
                }
            }
        }
    }
}
