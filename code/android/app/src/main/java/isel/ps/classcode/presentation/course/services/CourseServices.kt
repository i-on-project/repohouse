package isel.ps.classcode.presentation.course.services

import isel.ps.classcode.domain.UpdateLeaveCourseCompositeInput
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.utils.Either

/**
 * Interface defining the services available for the Course feature.
 */
interface CourseServices {
    suspend fun getClassrooms(courseId: Int): Either<HandleClassCodeResponseError, ClassroomsAndLeaveCourseRequests>
    suspend fun leaveCourseInGitHub(orgName: String, username: String): Either<HandleGitHubResponseError, Unit>
    suspend fun deleteTeamFromTeamInGitHub(courseName: String, teamSlug: String): Either<HandleGitHubResponseError, Unit>
    suspend fun updateLeaveCourseCompositeInClassCode(input: UpdateLeaveCourseCompositeInput, userId: Int): Either<HandleClassCodeResponseError, Unit>
}
