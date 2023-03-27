package com.isel.leic.ps.ion_classcode.http.pipeline

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
        private const val AUTHORIZATION_COOKIE_NAME = "ClassCodeAuthorization"
    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpServletRequest = request as HttpServletRequest
        val token = WebUtils.getCookie(httpServletRequest, AUTHORIZATION_COOKIE_NAME)?.value
        val user = authorizationHeaderProcessor.process(token)

        if (user == null) {
            TODO("Redirect to login page or return 401")
        } else {
            val mutableRequest = MutableHttpServletRequest(httpServletRequest)
            UserArgumentResolver.addUserTo(user, mutableRequest)
            chain?.doFilter(mutableRequest, response)
        }
    }
}
