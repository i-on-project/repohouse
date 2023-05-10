package com.isel.leic.ps.ionClassCode.http

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.springframework.stereotype.Component
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Interface to be implemented by classes that make http calls.
 */
interface Caller {
    suspend fun <T : Any> makeCallToObject(request: Request, kClass: Class<T>): T
    suspend fun <T : Any> makeCallToList(request: Request, kClass: Class<T>): List<T>
}

suspend inline fun <reified T : Any> Caller.makeCallToObject(request: Request): T = makeCallToObject(request, T::class.java)
suspend inline fun <reified T : Any> Caller.makeCallToList(request: Request): List<T> = makeCallToList(request, T::class.java)

/**
 * OkHttp's implementation of Caller interface.
 * @param okHttpClient client to be used to make requests
 * @param jsonMapper mapper to be used to map json responses to objects
 */
@Component
class OkHttp(
    private val okHttpClient: OkHttpClient,
    private val jsonMapper: ObjectMapper,
) : Caller {

    private suspend fun send(request: Request): String? {
        val response = suspendCoroutine { continuation ->
            okHttpClient.newCall(request = request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response)
                }
            })
        }

        return response.body?.string()
    }

    override suspend fun <T : Any> makeCallToObject(request: Request, kClass: Class<T>): T {
        val body = send(request)
        try {
            return jsonMapper.readValue(body, kClass)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun <T : Any> makeCallToList(request: Request, kClass: Class<T>): List<T> {
        val body = send(request)
        val listType = jsonMapper.typeFactory.constructCollectionType(ArrayList::class.java, kClass)
        try {
            return jsonMapper.readValue(body, listType)
        } catch (e: Exception) {
            throw e
        }
    }
}
