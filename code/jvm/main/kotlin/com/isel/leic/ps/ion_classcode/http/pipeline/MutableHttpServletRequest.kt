package com.isel.leic.ps.ion_classcode.http.pipeline

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper

class MutableHttpServletRequest(request: HttpServletRequest?) : HttpServletRequestWrapper(request) {

    private val customHeaders: MutableMap<String, String> = mutableMapOf()

    init {
        request?.headerNames?.toList()?.forEach { name ->
            putHeader(name, request.getHeader(name))
        }
    }

    fun putHeader(name: String, value: String) {
        customHeaders[name] = value
    }

    override fun getHeader(name: String): String {
        val headerValue = customHeaders[name]
        return headerValue ?: (request as HttpServletRequest).getHeader(name)
    }
}
