package com.isel.leic.ps.ionClassCode.http.pipeline

import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.http.controllers.web.AUTHORIZATION_COOKIE_NAME
import com.isel.leic.ps.ionClassCode.utils.cypher.AESDecrypt
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.WebUtils

class AuthenticationFailed : Exception()

/**
 * Authorization Interceptor that guarantees authentication for operations that require authentication.
 * @param authorizationHeaderProcessor Request Authorization Header Bearer Token Processor
 */
@Component
class AuthenticationInterceptor(
    private val authorizationHeaderProcessor: AuthorizationHeaderProcessor
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod && handler.methodParameters.any { it.parameterType == User::class.java }) {
            val tokenEncrypted = WebUtils.getCookie(request, AUTHORIZATION_COOKIE_NAME)?.value
            val token = tokenEncrypted?.let { AESDecrypt.decrypt(it) }
            val user = authorizationHeaderProcessor.process(token)
            return if (user == null) {
                throw AuthenticationFailed()
            } else {
                UserArgumentResolver.addUserTo(user, request)
                true
            }
        }
        return true
    }
}