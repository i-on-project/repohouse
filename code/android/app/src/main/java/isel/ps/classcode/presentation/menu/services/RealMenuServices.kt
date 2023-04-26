package isel.ps.classcode.presentation.menu.services

import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.GITHUB_API_BASE_URL
import isel.ps.classcode.GITHUB_USERINFO_URI
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.domain.Course
import isel.ps.classcode.domain.GitHubError
import isel.ps.classcode.domain.UserInfo
import isel.ps.classcode.domain.deserialization.UserInfoDeserialization
import isel.ps.classcode.http.HandleGitHubResponseError
import isel.ps.classcode.http.handleResponseGitHub
import isel.ps.classcode.http.send
import isel.ps.classcode.presentation.utils.ClassCodeResponseServicesError
import isel.ps.classcode.presentation.utils.Either
import isel.ps.classcode.presentation.utils.GitHubResponseServicesError
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.Request

class RealMenuServices(private val sessionStore: SessionStore, private val objectMapper: ObjectMapper, private val httpClient: OkHttpClient) : MenuServices {
    override suspend fun getUserInfo(): Either<GitHubResponseServicesError, UserInfo> {
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
            is Either.Left -> {
                when (result.value) {
                    is HandleGitHubResponseError.FailRequest -> Either.Left(value = GitHubResponseServicesError.FailGitHub(error = GitHubError(message=  result.value.error.message)))
                    is HandleGitHubResponseError.FailDeserialize -> Either.Left(value = GitHubResponseServicesError.FailDeserialization(error = result.value.error))
                }
            }
        }
    }

    override suspend fun getCourses(): Either<ClassCodeResponseServicesError, List<Course>> {
        TODO("Not yet implemented")
    }

    override fun logout() {
        TODO("Not yet implemented")
    }
}
