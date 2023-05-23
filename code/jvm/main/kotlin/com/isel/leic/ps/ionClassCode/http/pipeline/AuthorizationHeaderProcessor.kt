package com.isel.leic.ps.ionClassCode.http.pipeline

import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.services.UserServices
import com.isel.leic.ps.ionClassCode.utils.Result
import org.springframework.stereotype.Component

/**
 * Request Authorization Header Bearer Token Processor.
 * @param userService services to be used to obtain request header data
 */
@Component
class AuthorizationHeaderProcessor(
    private val userService: UserServices
) {
    /**
     * Processes the request header and returns the user if the token is valid.
     */
    fun process(token: String?): User? {
        if (token == null) return null
        return when (val authorization = userService.checkAuthentication(token)) {
            is Result.Success -> authorization.value
            is Result.Problem -> null
        }
    }
}
