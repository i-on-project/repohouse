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
import isel.ps.classcode.domain.LeaveCourse
import isel.ps.classcode.domain.LeaveTeam
import isel.ps.classcode.domain.LeaveTeamWithDelete
import isel.ps.classcode.domain.UpdateCompositeState
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
                                leaveTeamRequests = leaveCourseRequest.leaveTeamRequests,
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

    private fun leaveCourseInGitHub(leaveCourse: LeaveCourse, leaveTeamRequests: List<LeaveTeam>, activity: Activity) {
        viewModelScope.launch {
            when (val result = gitHubService.leaveCourseInGitHub(orgName = course.name, username = leaveCourse.githubUsername)) {
                is Either.Right -> {
                    val list = mutableListOf<LeaveTeamWithDelete>()
                    leaveTeamRequests.forEach { leaveTeam ->
                        if (leaveTeam.membersCount == 1) {
                            when (gitHubService.deleteTeamFromTeamInGitHub(courseName = course.name, teamSlug = leaveTeam.teamName.replace(" ", "-"))) {
                                is Either.Right -> {
                                    list.add(LeaveTeamWithDelete(requestId = leaveTeam.requestId, teamId = leaveTeam.teamId, state = "Accepted", wasDeleted = true))
                                }
                                is Either.Left -> {
                                    list.add(LeaveTeamWithDelete(requestId = leaveTeam.requestId, teamId = leaveTeam.teamId, state = leaveTeam.state))
                                }
                            }
                        } else {
                            list.add(LeaveTeamWithDelete(requestId = leaveTeam.requestId, teamId = leaveTeam.teamId, state = "Accepted"))
                        }
                    }
                    when (val res = courseServices.updateLeaveCourseCompositeInClassCode(input = UpdateLeaveCourseCompositeInput(composite = UpdateCompositeState(requestId = leaveCourse.composite), leaveCourse = UpdateLeaveCourse(requestId = leaveCourse.requestId, courseId = leaveCourse.courseId), leaveTeam = list), userId = leaveCourse.creator)) {
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
