package com.isel.leic.ps.ion_classcode.http.pipeline

import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.services.UserServices
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

/**
 * Request Authorization Header Bearer Token Processor.
 * @param userService services to be used to obtain request header data
 */
@Component
class AuthorizationHeaderProcessor(
    private val userService: UserServices
) {
    fun process(token: String?): User? {
        if (token == null) return null
        return when (val authorization = userService.checkAuthentication(token)) {
            is Either.Right -> authorization.value
            is Either.Left -> null
        }
    }
}
