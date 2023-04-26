package isel.ps.classcode.presentation.login.services

import android.graphics.Bitmap
import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.ACCESS_TOKEN_ENDPOINT
import isel.ps.classcode.BuildConfig
import isel.ps.classcode.GITHUB_BASE_URL
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.domain.AuthInfo
import isel.ps.classcode.domain.GitHubError
import isel.ps.classcode.domain.deserialization.AuthInfoDeserialization
import isel.ps.classcode.http.HandleGitHubResponseError
import isel.ps.classcode.http.handleResponseGitHub
import isel.ps.classcode.http.send
import isel.ps.classcode.presentation.utils.Either
import isel.ps.classcode.presentation.utils.GitHubResponseServicesError
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class RealGithubLoginServices(private val httpClient: OkHttpClient, val objectMapper: ObjectMapper, val sessionStore: SessionStore) :
    LoginServices {
    override suspend fun tradeAndStoreAccessToken(code: String): Either<GitHubResponseServicesError, AuthInfo> {
        val requestBody = FormBody.Builder()
            .add("client_id", BuildConfig.CLIENT_ID)
            .add("client_secret", BuildConfig.CLIENT_SECRET)
            .add("code", code)
            .build()

        val request = Request.Builder()
            .url("$GITHUB_BASE_URL$ACCESS_TOKEN_ENDPOINT")
            .post(requestBody)
            .addHeader("Accept", "application/json")
            .build()

        val result = request.send(httpClient) { response ->
            handleResponseGitHub<AuthInfoDeserialization>(response = response, jsonMapper = objectMapper)
        }

        return when (result) {
            is Either.Right -> {
                sessionStore.storeGithubToken(token = result.value.accessToken)
                Either.Right(value = AuthInfo(accessToken = result.value.accessToken))
            }
            is Either.Left -> {
                when (result.value) {
                    is HandleGitHubResponseError.FailRequest -> Either.Left(value = GitHubResponseServicesError.FailGitHub(error = GitHubError(message=  result.value.error.message)))
                    is HandleGitHubResponseError.FailDeserialize -> Either.Left(value = GitHubResponseServicesError.FailDeserialization(error = result.value.error))
                }
            }
        }
    }
}

