package isel.ps.classcode.presentation.login.services

import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.CLASSCODE_TOKEN_URL
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.domain.deserialization.ClassCodeAuthDto
import isel.ps.classcode.domain.deserialization.ClassCodeAuthDtoType
import isel.ps.classcode.http.handleSirenResponseClassCode
import isel.ps.classcode.http.send
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Helper class, just to store the result of the request with the header needed
 */
private data class Storage(val body: Either<HandleClassCodeResponseError, ClassCodeAuthDto>, val sessionCookie: String = "")

/**
 * Implementation of the [LoginServices] interface that will be used for the real app
 */
class RealLoginServices(private val httpClient: OkHttpClient, private val objectMapper: ObjectMapper, private val sessionStore: SessionStore) :
    LoginServices {
    override suspend fun getTheTokens(code: String, githubId: String): Either<HandleClassCodeResponseError, Unit> {
        val request = Request.Builder()
            .url(CLASSCODE_TOKEN_URL(code, githubId))
            .build()
        val result = request.send(httpClient) { response ->
            val headerName = "Set-Cookie"
            val sessionCookie = response.headers[headerName] ?: return@send Storage(body = Either.Left(value = HandleClassCodeResponseError.FailToGetTheHeader(error = "Fail to get the value in header $headerName")))
            val body = handleSirenResponseClassCode<ClassCodeAuthDto>(response = response, type = ClassCodeAuthDtoType, jsonMapper = objectMapper)
            return@send Storage(sessionCookie  = sessionCookie, body = body)
        }
        return when(result.body) {
            is Either.Left -> Either.Left(value = result.body.value)
            is Either.Right -> {
                val authInfo = result.body.value.properties
                sessionStore.storeClassCodeSessionCookie(token = result.sessionCookie)
                sessionStore.storeGithubToken(token = authInfo.accessToken)
                Either.Right(value = Unit)
            }
        }
    }
}

