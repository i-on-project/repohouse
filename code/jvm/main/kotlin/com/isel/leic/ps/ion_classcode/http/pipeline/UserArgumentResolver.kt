package com.isel.leic.ps.ion_classcode.http.pipeline

import com.isel.leic.ps.ion_classcode.domain.User
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class UserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter) = parameter.parameterType == User::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java) as MutableHttpServletRequest // TODO ?: throw FailException("Obtain request failed")
        return getUserFrom(request) // TODO ?: throw FailException("Server error")
    }

    companion object {
        private const val KEY_HEADER = "User"
        private const val KEY_ATTRIBUTE = "UserArgumentResolver"

        fun addUserTo(user: User, request: MutableHttpServletRequest) {
            request.setAttribute(KEY_ATTRIBUTE, user)
            return request.putHeader(KEY_HEADER, user::class.simpleName.toString())
        }

        fun getUserFrom(request: MutableHttpServletRequest): User? {
            return request.getAttribute(KEY_ATTRIBUTE)?.let {
                it as? User
            }
        }
    }
}
