package isel.ps.classcode.dataAccess.gitHubService

import isel.ps.classcode.domain.CreateRepo
import isel.ps.classcode.domain.CreateTeamComposite
import isel.ps.classcode.domain.LeaveTeam
import isel.ps.classcode.domain.UserInfo
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.utils.Either
import java.io.InputStream

/**
 * Implementation of the [GitHubService] interface that returns fake data.
 */
class FakeGitHubService : GitHubService {
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

    override suspend fun archiveRepoInGithub(
        orgName: String,
        repoName: String,
    ): Either<HandleGitHubResponseError, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun leaveCourseInGitHub(
        orgName: String,
        username: String,
    ): Either<HandleGitHubResponseError, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTeamFromTeamInGitHub(
        courseName: String,
        teamSlug: String,
    ): Either<HandleGitHubResponseError, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserInfo(): Either<HandleGitHubResponseError, UserInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun removeMemberFromTeamInGitHub(
        leaveTeam: LeaveTeam,
        courseName: String,
        teamSlug: String,
    ): Either<HandleGitHubResponseError, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getImageFromGitHub(url: String): InputStream? {
        TODO("Not yet implemented")
    }
}
