package com.isel.leic.ps.ion_repohouse.http

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend inline fun <reified T> Request.send(okHttpClient: OkHttpClient, jsonMapper: ObjectMapper): T {
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

    val body = response.body?.string()
    try {
        return jsonMapper.readValue(body, T::class.java)
    }
    catch (e: Exception) {
        throw e
    }
}
