package com.isel.leic.ps.ion_classcode.http.pipeline

import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.services.UserServices
import org.springframework.stereotype.Component

@Component
class AuthorizationHeaderProcessor(
    private val userService: UserServices
) {

    fun process(token: String?): User? {
        // if (token == null) return null
        return userService.checkAuthenticationDummy("dummyToken")
    }
}
