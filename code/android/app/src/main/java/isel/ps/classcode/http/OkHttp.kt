package isel.ps.classcode.http

import com.fasterxml.jackson.core.exc.StreamReadException
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.domain.GitHubError
import isel.ps.classcode.domain.ProblemJson
import isel.ps.classcode.domain.deserialization.GithubErrorDeserialization
import isel.ps.classcode.domain.deserialization.ProblemJsonDeserialization
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
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

inline fun <reified R : Any> handleResponseGitHub(response: Response, jsonMapper: ObjectMapper, ignoreBody: Boolean = false): Either<HandleGitHubResponseError, R> {
    val body = response.body?.string()
    return if (response.isSuccessful) {
        if (ignoreBody) {
            Either.Right(value = Unit as R)
        } else {
            try {
                Either.Right(value = jsonMapper.readValue(body, R::class.java))
            } catch (e: StreamReadException) {
                Either.Left(value = HandleGitHubResponseError.FailDeserialize(error = "Failed to deserialize response body: $body"))
            }
        }
    } else {
        try {
            val githubErrorDeserialization = jsonMapper.readValue(body, GithubErrorDeserialization::class.java)
            Either.Left(value = HandleGitHubResponseError.FailRequest(error = GitHubError(githubErrorDeserialization = githubErrorDeserialization)))
        } catch (e: StreamReadException) {
            Either.Left(value = HandleGitHubResponseError.FailDeserialize(error = "Failed to deserialize error response body: $body"))
        }
    }
}

/**
 * Handle the response from the ClassCode API.
 */

inline fun <reified R : Any> handleSirenResponseClassCode(response: Response, type: JavaType? = null, jsonMapper: ObjectMapper, ignoreBody: Boolean = false): Either<HandleClassCodeResponseError, R> {
    val body = response.body?.string()
    return if (response.isSuccessful) {
        if (ignoreBody) {
            Either.Right(value = Unit as R)
        } else {
            try {
                Either.Right(value = jsonMapper.readValue(body, type))
            } catch (e: StreamReadException) {
                Either.Left(value = HandleClassCodeResponseError.FailDeserialize(error = "Failed to deserialize response body: $body"))
            }
        }
    } else {
        try {
            val problemJsonDeserialization = jsonMapper.readValue(body, ProblemJsonDeserialization::class.java)
            Either.Left(
                value = HandleClassCodeResponseError.FailRequest(
                    error = ProblemJson(problemJsonDeserialization = problemJsonDeserialization),
                ),
            )
        } catch (e: StreamReadException) {
            Either.Left(value = HandleClassCodeResponseError.FailDeserialize(error = "Failed to deserialize error response body: $body"))
        }
    }
}
