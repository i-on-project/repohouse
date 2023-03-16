package com.isel.leic.ps.ion_repohouse.http.pipeline

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.springframework.stereotype.Component

/**
 * Filter only use to log incoming requests and responses to those requests
 * @param logger logger instance
 */
@Component
class LoggerFilter(
    private val logger: Logger,
) : HttpFilter() {

    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val reqModel = RequestModel(method = request.method, uri = request.requestURI)
        logRequest(request = reqModel, logger = logger)
        chain.doFilter(request, response)
        val resModel = ResponseModel(status = response.status, uri = request.requestURI)
        logResponse(response = resModel, logger = logger)
    }

    companion object {
        /**
         * Logs an incoming request.
         * @param request request data representation
         * @param logger logger instance
         */
        fun logRequest(request: RequestModel, logger: Logger) {
            logger.info("Request: Method [\"${request.method}\"], URI [\"${request.uri}\"]")
        }

        /**
         * Logs an incoming response.
         * @param response response data representation
         * @param logger logger instance
         */
        fun logResponse(response: ResponseModel, logger: Logger) {
            logger.info("Response: Status [\"${response.status}\"], URI [\"${response.uri}\"]")
        }

        /**
         * Representation of request data.
         * @param method request method type
         * @param uri request associated uri
         */
        data class RequestModel(
            val method: String,
            val uri: String,
        )

        /**
         * Representation of response data.
         * @param status response status code
         * @param uri request associated uri
         */
        data class ResponseModel(
            val status: Int,
            val uri: String,
        )
    }
}
