package isel.ps.classcode.presentation.classroom.services

import isel.ps.classcode.domain.ArchiveRepo
import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.GetAssignmentsResponse
import isel.ps.classcode.domain.Teams
import isel.ps.classcode.domain.UpdateArchiveRepoInput
import isel.ps.classcode.domain.UpdateCreateTeamStatusInput
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
        delay(2000)
        return Either.Right(
            value = GetAssignmentsResponse(
                assignments = listOf(
                    Assignment(id = 1, classroomId = 1, description = "Description1", title = "Assignment1"),
                    Assignment(id = 2, classroomId = 1, description = "Description2", title = "Assignment2"),
                ),
                archiveRepos = listOf(
                    ArchiveRepo(requestId = 1, creator = 1, state = "Pending", composite = 1, repoId = 1, repoName = "Repo1"),
                ),
            ),
        )
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
}
