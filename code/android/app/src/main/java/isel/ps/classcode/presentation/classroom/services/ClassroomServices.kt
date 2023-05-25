package isel.ps.classcode.presentation.classroom.services

import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.CreateTeamComposite
import isel.ps.classcode.domain.CreateRepo
import isel.ps.classcode.domain.Teams
import isel.ps.classcode.domain.UpdateCreateTeamStatusInput
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

/**
 * Interface defining the services available for the Classroom feature.
 */
interface ClassroomServices {
    suspend fun getAssignments(classroomId: Int, courseId: Int): Either<HandleClassCodeResponseError, List<Assignment>>
    suspend fun getTeams(classroomId: Int, courseId: Int, assignmentId: Int): Either<HandleClassCodeResponseError, Teams>
    suspend fun createTeamInGitHub(createTeamComposite: CreateTeamComposite, orgName: String): ResultFromRequest<Int>
    suspend fun addMemberToTeamInGitHub(orgName: String, teamSlug: String, username: String): ResultFromRequest<Unit>
    suspend fun createRepoInGitHub(orgName: String, teamId: Int?, repo: CreateRepo): ResultFromRequest<String>
    suspend fun changeCreateTeamStatus(classroomId: Int, courseId: Int, assignmentId: Int, teamId: Int, updateCreateTeamStatus: UpdateCreateTeamStatusInput): Either<HandleClassCodeResponseError, Unit>
}