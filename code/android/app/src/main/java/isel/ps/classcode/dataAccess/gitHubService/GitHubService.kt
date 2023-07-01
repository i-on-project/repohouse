package isel.ps.classcode.dataAccess.gitHubService

import isel.ps.classcode.domain.CreateRepo
import isel.ps.classcode.domain.CreateTeamComposite
import isel.ps.classcode.domain.LeaveTeam
import isel.ps.classcode.domain.UserInfo
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.utils.Either
import java.io.InputStream

/**
 * Interface defining the services available for the GitHub feature.
 */
interface GitHubService {
    suspend fun createTeamInGitHub(createTeamComposite: CreateTeamComposite, orgName: String): Either<HandleGitHubResponseError, Int>
    suspend fun addMemberToTeamInGitHub(orgName: String, teamSlug: String, username: String): Either<HandleGitHubResponseError, Unit>
    suspend fun createRepoInGitHub(orgName: String, teamId: Int?, repo: CreateRepo): Either<HandleGitHubResponseError, String?>
    suspend fun archiveRepoInGithub(orgName: String, repoName: String): Either<HandleGitHubResponseError, Unit>
    suspend fun leaveCourseInGitHub(orgName: String, username: String): Either<HandleGitHubResponseError, Unit>
    suspend fun deleteTeamFromTeamInGitHub(courseName: String, teamSlug: String): Either<HandleGitHubResponseError, Unit>
    suspend fun getUserInfo(): Either<HandleGitHubResponseError, UserInfo>
    suspend fun removeMemberFromTeamInGitHub(leaveTeam: LeaveTeam, courseName: String, teamSlug: String): Either<HandleGitHubResponseError, Unit>
    suspend fun getImageFromGitHub(url: String): InputStream?
}
