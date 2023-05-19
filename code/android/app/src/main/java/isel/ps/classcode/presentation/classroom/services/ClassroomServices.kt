package isel.ps.classcode.presentation.classroom.services

import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.Teams
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.utils.Either

/**
 * Interface defining the services available for the Classroom feature.
 */
interface ClassroomServices {
    suspend fun getAssignments(classroomId: Int, courseId: Int): Either<HandleClassCodeResponseError, List<Assignment>>
    suspend fun getTeams(classroomId: Int, courseId: Int, assignmentId: Int): Either<HandleClassCodeResponseError, Teams>
    suspend fun createTeamInGitHub(orgName: String, teamName: String): Either<HandleGitHubResponseError, Unit>
    suspend fun changeCreateTeamStatus(classroomId: Int, courseId: Int, assignmentId: Int, teamId: Int, state: String): Either<HandleClassCodeResponseError, Unit>
}