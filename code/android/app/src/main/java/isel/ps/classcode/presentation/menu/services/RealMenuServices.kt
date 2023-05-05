package isel.ps.classcode.presentation.menu.services

import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.CLASSCODE_MENU_URL
import isel.ps.classcode.GITHUB_API_BASE_URL
import isel.ps.classcode.GITHUB_USERINFO_URI
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.domain.UserInfo
import isel.ps.classcode.domain.deserialization.ClassCodeMenuDto
import isel.ps.classcode.domain.deserialization.ClassCodeMenuDtoType
import isel.ps.classcode.domain.deserialization.UserInfoDeserialization
import isel.ps.classcode.http.handleResponseGitHub
import isel.ps.classcode.http.handleSirenResponseClassCode
import isel.ps.classcode.http.send
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * The implementation of menu services that will be used on the final product
 */

class RealMenuServices(private val sessionStore: SessionStore, private val objectMapper: ObjectMapper, private val httpClient: OkHttpClient) : MenuServices {
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

    override suspend fun getCourses(): Either<HandleClassCodeResponseError, Unit> {
        val cookie = sessionStore.getSessionCookie()
        val request = Request.Builder()
            .url(CLASSCODE_MENU_URL)
            .addHeader("Cookie", cookie.first())
            .build()
        val result = request.send(httpClient) { response ->
            handleSirenResponseClassCode<ClassCodeMenuDto>(response = response, type = ClassCodeMenuDtoType, jsonMapper = objectMapper)
        }
        return when (result) {
            is Either.Right -> {
                Either.Right(Unit)
            }
            is Either.Left -> Either.Left(value = result.value)
        }
    }

    override fun logout() {
        TODO("Not yet implemented")
    }
}
