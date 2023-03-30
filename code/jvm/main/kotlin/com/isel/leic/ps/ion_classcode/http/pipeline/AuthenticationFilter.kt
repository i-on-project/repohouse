package com.isel.leic.ps.ion_classcode.http.pipeline

import com.isel.leic.ps.ion_classcode.utils.cypher.AESDecrypt
import com.isel.leic.ps.ion_classcode.utils.cypher.AESEncrypt
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import org.springframework.web.util.WebUtils

@Component
class AuthenticationFilter(
    private val authorizationHeaderProcessor: AuthorizationHeaderProcessor
) : Filter {

    companion object {
        private const val AUTHORIZATION_COOKIE_NAME = "Session"
    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpServletRequest = request as HttpServletRequest
        val tokenEncrypted = WebUtils.getCookie(httpServletRequest, AUTHORIZATION_COOKIE_NAME)?.value
        val token = tokenEncrypted?.let {
            AESDecrypt().decrypt(it)
        }
        val user = authorizationHeaderProcessor.process(token)
        if (user != null) {
            UserArgumentResolver.addUserTo(user, httpServletRequest)
        }
        chain?.doFilter(httpServletRequest, response)
    }
}
