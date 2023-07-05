package isel.ps.classcode.presentation.classroom.services

import isel.ps.classcode.domain.GetAssignmentsResponse
import isel.ps.classcode.domain.Teams
import isel.ps.classcode.domain.UpdateArchiveRepoInput
import isel.ps.classcode.domain.UpdateCreateTeamStatusInput
import isel.ps.classcode.domain.UpdateLeaveClassroomCompositeInput
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.delay

/**
 * Implementation of the [ClassroomServices] interface that will be used for tests
 */
class FakeClassroomServices : ClassroomServices {
    override suspend fun getAssignments(
        classroomId: Int,
        courseId: Int,
    ): Either<HandleClassCodeResponseError, GetAssignmentsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getTeams(
        classroomId: Int,
        courseId: Int,
        assignmentId: Int,
    ): Either<HandleClassCodeResponseError, Teams> {
        delay(2000)
        TODO("Not yet implemented")
    }

    override suspend fun changeCreateTeamStatus(
        classroomId: Int,
        courseId: Int,
        assignmentId: Int,
        teamId: Int,
        updateCreateTeamStatus: UpdateCreateTeamStatusInput,
    ): Either<HandleClassCodeResponseError, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun changeStatusArchiveRepoInClassCode(
        courseId: Int,
        classroomId: Int,
        updateArchiveRepo: UpdateArchiveRepoInput,
    ): Either<HandleClassCodeResponseError, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateLeaveClassroomCompositeInClassCode(
        input: UpdateLeaveClassroomCompositeInput,
        courseId: Int,
        userId: Int
    ): Either<HandleClassCodeResponseError, Unit> {
        TODO("Not yet implemented")
    }
}
