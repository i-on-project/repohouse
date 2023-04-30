package isel.ps.classcode.presentation.login.services

import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.CLASSCODE_AUTH_URL
import isel.ps.classcode.CLASSCODE_CALLBACK_URL
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.dataAccess.userInfoStore.UserInfoStore
import isel.ps.classcode.domain.AuthInfo
import isel.ps.classcode.domain.UserInfo
import isel.ps.classcode.domain.deserialization.ClassCodeAuthDeserialization
import isel.ps.classcode.domain.deserialization.ClassCodeAuthDto
import isel.ps.classcode.http.handleCallbackResponseClassCode
import isel.ps.classcode.http.handleSirenResponseClassCode
import isel.ps.classcode.http.send
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleRedirectClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * The implementation of login services that will be used on the final product
 */

data class RequestInfo(val url: String,  val stateCookie: String)

class RealGithubLoginServices(private val httpClient: OkHttpClient, private val objectMapper: ObjectMapper, private val sessionStore: SessionStore) :
    LoginServices {
    override suspend fun auth(): Either<HandleClassCodeResponseError, RequestInfo> {
        val request = Request.Builder()
            .url(CLASSCODE_AUTH_URL)
            .build()
        return request.send(httpClient) { response ->
            val headerName = "Set-Cookie"
            val stateCookie = response.headers[headerName] ?: return@send Either.Left(value = HandleClassCodeResponseError.FailToGetTheHeader(error = "Fail to get the value in header $headerName"))
            when (val body = handleSirenResponseClassCode<ClassCodeAuthDto, ClassCodeAuthDeserialization>(response = response,jsonMapper = objectMapper)){
                is Either.Right -> Either.Right(value = RequestInfo(url = body.value.properties.url, stateCookie = stateCookie))
                is Either.Left -> Either.Left(value = body.value)
            }
        }
    }

    override suspend fun tradeAndStoreAccessToken(code: String, stateCookie: String, state: String): Either<HandleRedirectClassCodeResponseError, AuthInfo> {
        val request = Request.Builder()
            .url("$CLASSCODE_CALLBACK_URL?code=$code&state=$state")
            .addHeader("Cookie", stateCookie)
            .build()

        val result = request.send(httpClient) { response ->
            handleCallbackResponseClassCode(response = response, jsonMapper = objectMapper)
        }

        return when (result) {
            is Either.Right -> {
                if (result.value.loginResponse != null && result.value.cookie != null) {
                    sessionStore.storeClassCodeToken(token = result.value.cookie)
                    sessionStore.storeGithubToken(token = result.value.loginResponse.tokenInfo.accessToken)
                    Either.Right(value = AuthInfo(accessToken = result.value.loginResponse.tokenInfo.accessToken))
                }else {
                    Either.Left(value = HandleRedirectClassCodeResponseError.Fail(error = "The cookie or the response body were null when its not supposed to be"))
                }
            }
            is Either.Left -> Either.Left(value = result.value)
        }
    }
}

