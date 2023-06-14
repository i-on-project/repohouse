package isel.ps.classcode.dataAccess.gitHubService

import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.DELETE_TEAM
import isel.ps.classcode.GITHUB_ADD_MEMBER_TO_TEAM
import isel.ps.classcode.GITHUB_ADD_TEAM
import isel.ps.classcode.GITHUB_API_BASE_URL
import isel.ps.classcode.GITHUB_CREATE_REPO
import isel.ps.classcode.GITHUB_DELETE_TEAM
import isel.ps.classcode.GITHUB_REMOVE_MEMBER_FROM_TEAM
import isel.ps.classcode.GITHUB_UPDATE_REPO
import isel.ps.classcode.GITHUB_USERINFO_URI
import isel.ps.classcode.MEDIA_TYPE
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.domain.CreateRepo
import isel.ps.classcode.domain.CreateTeamComposite
import isel.ps.classcode.domain.LeaveTeam
import isel.ps.classcode.domain.UserInfo
import isel.ps.classcode.domain.deserialization.GitHubCreateRepoDeserialization
import isel.ps.classcode.domain.deserialization.GitHubCreateTeamDeserialization
import isel.ps.classcode.domain.deserialization.UserInfoDeserialization
import isel.ps.classcode.http.handleResponseGitHub
import isel.ps.classcode.http.send
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

/**
 * Implementation of the [GitHubService] interface that returns real data.
 */

class RealGitHubService(private val sessionStore: SessionStore, private val objectMapper: ObjectMapper, private val httpClient: OkHttpClient) :
    GitHubService {

    override suspend fun createTeamInGitHub(
        createTeamComposite: CreateTeamComposite,
        orgName: String,
    ): Either<HandleGitHubResponseError, Int> {
        val accessToken = sessionStore.getGithubToken().first()
        val requestCreateTeam = Request.Builder()
            .url(GITHUB_ADD_TEAM(orgName))
            .post(
                objectMapper.writeValueAsString(
                    mapOf(
                        "name" to createTeamComposite.createTeam.teamName,
                        "permission" to "push",
                    ),
                ).toRequestBody(MEDIA_TYPE),
            )
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        val result = requestCreateTeam.send(httpClient) { response ->
            handleResponseGitHub<GitHubCreateTeamDeserialization>(
                response = response,
                jsonMapper = objectMapper,
            )
        }
        return when (result) {
            is Either.Left -> Either.Left(value = result.value)
            is Either.Right -> Either.Right(value = result.value.id)
        }
    }
    override suspend fun addMemberToTeamInGitHub(
        orgName: String,
        teamSlug: String,
        username: String,
    ): Either<HandleGitHubResponseError, Unit> {
        val accessToken = sessionStore.getGithubToken().first()
        val request = Request.Builder()
            .url(GITHUB_ADD_MEMBER_TO_TEAM(orgName, teamSlug, username))
            .put(
                objectMapper.writeValueAsString(
                    emptyMap<String, String>(),
                ).toRequestBody(MEDIA_TYPE),
            )
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        return request.send(httpClient) { response ->
            handleResponseGitHub(response = response, jsonMapper = objectMapper, ignoreBody = true)
        }
    }

    override suspend fun createRepoInGitHub(orgName: String, teamId: Int?, repo: CreateRepo): Either<HandleGitHubResponseError, String?> {
        if (teamId == null) return Either.Right(value = null)
        val accessToken = sessionStore.getGithubToken().first()
        val request = Request.Builder()
            .url(GITHUB_CREATE_REPO(orgName))
            .post(
                objectMapper.writeValueAsString(
                    mapOf(
                        "name" to repo.repoName,
                        "team_id" to teamId,
                        "auto_init" to true,
                        "private" to true,
                    ),
                ).toRequestBody(MEDIA_TYPE),
            )
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        val result = request.send(httpClient) { response ->
            handleResponseGitHub<GitHubCreateRepoDeserialization>(
                response = response,
                jsonMapper = objectMapper,
            )
        }
        return when (result) {
            is Either.Right -> Either.Right(value = result.value.htmlUrl)
            is Either.Left -> Either.Left(value = result.value)
        }
    }
    override suspend fun archiveRepoInGithub(
        orgName: String,
        repoName: String,
    ): Either<HandleGitHubResponseError, Unit> {
        val accessToken = sessionStore.getGithubToken().first()
        val request = Request.Builder()
            .url(GITHUB_UPDATE_REPO(orgName, repoName))
            .post(
                objectMapper.writeValueAsString(
                    mapOf(
                        "archived" to true,
                    ),
                ).toRequestBody(MEDIA_TYPE),
            )
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        return request.send(httpClient) { response ->
            handleResponseGitHub(
                response = response,
                jsonMapper = objectMapper,
                ignoreBody = true,
            )
        }
    }

    override suspend fun leaveCourseInGitHub(orgName: String, username: String): Either<HandleGitHubResponseError, Unit> {
        val accessToken = sessionStore.getGithubToken().first()
        val requestCreateTeam = Request.Builder()
            .url(GITHUB_DELETE_TEAM(orgName, username))
            .delete(
                objectMapper.writeValueAsString(
                    emptyMap<String, String>(),
                ).toRequestBody(MEDIA_TYPE),
            )
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        val result = requestCreateTeam.send(httpClient) { response ->
            handleResponseGitHub<Unit>(
                response = response,
                jsonMapper = objectMapper,
                ignoreBody = true,
            )
        }
        return when (result) {
            is Either.Left -> Either.Left(value = result.value)
            is Either.Right -> Either.Right(value = Unit)
        }
    }

    override suspend fun deleteTeamFromTeamInGitHub(courseName: String, teamSlug: String): Either<HandleGitHubResponseError, Unit> {
        val accessToken = sessionStore.getGithubToken().first()
        val requestCreateTeam = Request.Builder()
            .url(DELETE_TEAM(courseName, teamSlug))
            .delete(
                objectMapper.writeValueAsString(
                    emptyMap<String, String>(),
                ).toRequestBody(MEDIA_TYPE),
            )
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        return requestCreateTeam.send(httpClient) { response ->
            handleResponseGitHub(response = response, jsonMapper = objectMapper, ignoreBody = true)
        }
    }

    override suspend fun getUserInfo(): Either<HandleGitHubResponseError, UserInfo> {
        val accessToken = sessionStore.getGithubToken().first()
        val request = Request.Builder()
            .url("$GITHUB_API_BASE_URL$GITHUB_USERINFO_URI")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .build()
        val result = request.send(httpClient) { response ->
            handleResponseGitHub<UserInfoDeserialization>(response = response, jsonMapper = objectMapper)
        }

        return when (result) {
            is Either.Right -> {
                Either.Right(value = UserInfo(userInfoDeserialization = result.value))
            }
            is Either.Left -> Either.Left(value = result.value)
        }
    }

    override suspend fun removeMemberFromTeamInGitHub(
        leaveTeam: LeaveTeam,
        courseName: String,
        teamSlug: String,
    ): Either<HandleGitHubResponseError, Unit> {
        val accessToken = sessionStore.getGithubToken().first()
        val request = Request.Builder()
            .url(GITHUB_REMOVE_MEMBER_FROM_TEAM(courseName, teamSlug, leaveTeam.githubUsername))
            .delete(
                objectMapper.writeValueAsString(
                    emptyMap<String, String>(),
                ).toRequestBody(MEDIA_TYPE),
            )
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        return request.send(httpClient) { response ->
            handleResponseGitHub(response = response, jsonMapper = objectMapper, ignoreBody = true)
        }
    }

    override suspend fun getImageFromGitHub(url: String): InputStream? {
        val request = Request.Builder()
            .url(url)
            .build()
        return request.send(httpClient) { response ->
            val body = response.body ?: return@send null
            if (response.isSuccessful) {
                body.byteStream()
            } else {
                null
            }
        }
    }
}

