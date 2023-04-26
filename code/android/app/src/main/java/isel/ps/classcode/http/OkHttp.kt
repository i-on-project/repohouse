package isel.ps.classcode.http

import com.fasterxml.jackson.core.exc.StreamReadException
import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.domain.deserialization.ProblemJsonDeserialization
import isel.ps.classcode.domain.deserialization.GithubErrorDeserialization
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

sealed class HandleGitHubResponseError {
    class FailDeserialize(val error: String) : HandleGitHubResponseError()
    class FailRequest(val error: GithubErrorDeserialization) : HandleGitHubResponseError()
}

sealed class HandleClassCodeResponseError {
    class FailDeserialize(val error: String) : HandleClassCodeResponseError()
    class FailRequest(val error: ProblemJsonDeserialization) : HandleClassCodeResponseError()
}

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

inline fun <reified R : Any> handleResponseClassCode(response: Response, jsonMapper: ObjectMapper): Either<HandleClassCodeResponseError, R> {
    val body = response.body?.string()
    return if (response.isSuccessful) {
        try {
            Either.Right(value = jsonMapper.readValue(body, R::class.java))
        }
        catch (e: StreamReadException) {
            Either.Left(value = HandleClassCodeResponseError.FailDeserialize(error = "Failed to deserialize response body: $body"))
        }
    }
    else {
        try {
            Either.Left(value = HandleClassCodeResponseError.FailRequest(error = jsonMapper.readValue(body, ProblemJsonDeserialization::class.java)))
        } catch (e: StreamReadException) {
            Either.Left(value = HandleClassCodeResponseError.FailDeserialize(error = "Failed to deserialize error response body: $body"))
        }
    }
}

/*
suspend fun <T : Any> makeCallToObject(okHttpClient: OkHttpClient, request: Request, kClass: Class<T>, jsonMapper: ObjectMapper): T {
    val body = send(okHttpClient, request)
    try {
        return jsonMapper.readValue(body, kClass)
    } catch (e: Exception) {
        throw e
    }
}

suspend fun <T : Any> makeCallToList(okHttpClient: OkHttpClient, request: Request, kClass: Class<T>, jsonMapper: ObjectMapper): List<T> {

    val body = send(okHttpClient, request)
    val listType = jsonMapper.typeFactory.constructCollectionType(ArrayList::class.java, kClass)
    try {
        return jsonMapper.readValue(body, listType)
    } catch (e: Exception) {
        throw e
    }
}

 */