package isel.ps.classcode.http

import com.fasterxml.jackson.core.exc.StreamReadException
import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.domain.CallBackResponse
import isel.ps.classcode.domain.deserialization.ProblemJsonDeserialization
import isel.ps.classcode.domain.deserialization.GithubErrorDeserialization
import isel.ps.classcode.domain.deserialization.LoginResponseDeserialization
import isel.ps.classcode.http.hypermedia.SirenEntity
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.http.utils.HandleRedirectClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Sends a request using the given [okHttpClient] and returns the response body as a string.
 * The handler function is used to handle the response body.
 */
suspend fun <T> Request.send(okHttpClient: OkHttpClient, handler: (Response) -> T): T {
    val response = suspendCoroutine { continuation ->
        okHttpClient.newCall(request = this).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response)
            }
        })
    }
    return handler(response)
}

/**
 * Handle the response from the GitHub API.
 */

inline fun <reified R : Any> handleResponseGitHub(response: Response, jsonMapper: ObjectMapper): Either<HandleGitHubResponseError, R> {
    val body = response.body?.string()
    return if (response.isSuccessful) {
        try {
            Either.Right(value = jsonMapper.readValue(body, R::class.java))
        }
        catch (e: StreamReadException) {
            Either.Left(value = HandleGitHubResponseError.FailDeserialize(error = "Failed to deserialize response body: $body"))
        }
    }
    else {
        try {
            Either.Left(value = HandleGitHubResponseError.FailRequest(error = jsonMapper.readValue(body, GithubErrorDeserialization::class.java)))
        } catch (e: StreamReadException) {
            Either.Left(value = HandleGitHubResponseError.FailDeserialize(error = "Failed to deserialize error response body: $body"))
        }
    }
}

/**
 * Handle the response from the ClassCode API.
 */

inline fun <reified R : Any, reified T> handleSirenResponseClassCode(response: Response, jsonMapper: ObjectMapper): Either<HandleClassCodeResponseError, R> {
    val body = response.body?.string()
    return if (response.isSuccessful) {
        try {
            Either.Right(value = jsonMapper.readValue(body, SirenEntity.getType<T>()))
        } catch (e: StreamReadException) {
            Either.Left(value = HandleClassCodeResponseError.FailDeserialize(error = "Failed to deserialize response body: $body"))
        }
    } else {
        try {
            Either.Left(
                value = HandleClassCodeResponseError.FailRequest(
                    error = jsonMapper.readValue(
                        body,
                        ProblemJsonDeserialization::class.java
                    )
                )
            )
        } catch (e: StreamReadException) {
            Either.Left(value = HandleClassCodeResponseError.FailDeserialize(error = "Failed to deserialize error response body: $body"))
        }
    }
}

fun handleCallbackResponseClassCode(response: Response, jsonMapper: ObjectMapper): Either<HandleRedirectClassCodeResponseError, CallBackResponse> {
    val cookie = response.headers["Set-Cookie"]
    val location = response.headers["Location"] ?: return Either.Left(value = HandleRedirectClassCodeResponseError.FailToGetTheLocation(error = "Failed to get the header Location"))
    return if (cookie != null) {
        val body = response.body?.string()
        try {
            val content = jsonMapper.readValue(body, LoginResponseDeserialization::class.java)
            Either.Right(value = CallBackResponse(loginResponse = content, cookie = cookie, deepLink = location))
        } catch (e: StreamReadException) {
            Either.Left(value = HandleRedirectClassCodeResponseError.FailDeserialize(error = "Failed to deserialize response body: $body"))
        }
    } else {
        Either.Left(value = HandleRedirectClassCodeResponseError.FailFromClasscode(error = CallBackResponse(deepLink = location)))
    }
}
