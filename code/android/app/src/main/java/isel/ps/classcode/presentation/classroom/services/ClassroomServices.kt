package isel.ps.classcode.presentation.classroom.services

import isel.ps.classcode.domain.GetAssignmentsResponse
import isel.ps.classcode.domain.Teams
import isel.ps.classcode.domain.UpdateArchiveRepoInput
import isel.ps.classcode.domain.UpdateCreateTeamStatusInput
import isel.ps.classcode.domain.UpdateLeaveClassroomCompositeInput
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

/**
 * Interface defining the services available for the Classroom feature.
 */
interface ClassroomServices {
    suspend fun getAssignments(classroomId: Int, courseId: Int): Either<HandleClassCodeResponseError, GetAssignmentsResponse>
    suspend fun getTeams(classroomId: Int, courseId: Int, assignmentId: Int): Either<HandleClassCodeResponseError, Teams>
    suspend fun changeCreateTeamStatus(classroomId: Int, courseId: Int, assignmentId: Int, teamId: Int, updateCreateTeamStatus: UpdateCreateTeamStatusInput): Either<HandleClassCodeResponseError, Unit>
    suspend fun changeStatusArchiveRepoInClassCode(courseId: Int, classroomId: Int, updateArchiveRepo: UpdateArchiveRepoInput): Either<HandleClassCodeResponseError, Unit>
    suspend fun updateLeaveClassroomCompositeInClassCode(input: UpdateLeaveClassroomCompositeInput, courseId: Int, userId: Int): Either<HandleClassCodeResponseError, Unit>
}
