package isel.ps.classcode.presentation.classroom.services

import isel.ps.classcode.domain.CreateRepo
import isel.ps.classcode.domain.CreateTeamComposite
import isel.ps.classcode.domain.GetAssignmentsResponse
import isel.ps.classcode.domain.Teams
import isel.ps.classcode.domain.UpdateArchiveRepoInput
import isel.ps.classcode.domain.UpdateCreateTeamStatusInput
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
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
        TODO()
    }

    override suspend fun createTeamInGitHub(
        createTeamComposite: CreateTeamComposite,
        orgName: String,
    ): Either<HandleGitHubResponseError, Int> {
        TODO("Not yet implemented")
    }

    override suspend fun addMemberToTeamInGitHub(
        orgName: String,
        teamSlug: String,
        username: String,
    ): Either<HandleGitHubResponseError, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun createRepoInGitHub(
        orgName: String,
        teamId: Int?,
        repo: CreateRepo,
    ): Either<HandleGitHubResponseError, String?> {
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

    override suspend fun archiveRepoInGithub(
        orgName: String,
        repoName: String,
    ): Either<HandleGitHubResponseError, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun changeStatusArchiveRepoInClassCode(
        courseId: Int,
        classroomId: Int,
        updateArchiveRepo: UpdateArchiveRepoInput
    ): Either<HandleClassCodeResponseError, Unit> {
        TODO("Not yet implemented")
    }
}
