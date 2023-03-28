package com.isel.leic.ps.ion_classcode.http.pipeline

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import java.util.*

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


    override fun getHeader(name: String): String? {
        return customHeaders[name] ?: super.getHeader(name)
    }

    override fun getHeaderNames(): Enumeration<String> {
        val names = super.getHeaderNames().toList() + customHeaders.keys
        return Collections.enumeration(names)
    }

    override fun getHeaders(name: String): Enumeration<String> {
        val values = customHeaders[name]?.let { listOf(it) } ?: emptyList()
        val parentValues = super.getHeaders(name).toList()
        return Collections.enumeration(values + parentValues)
    }

}
