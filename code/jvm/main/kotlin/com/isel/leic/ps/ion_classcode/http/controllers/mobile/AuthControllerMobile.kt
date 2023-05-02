package com.isel.leic.ps.ion_classcode.http.controllers.mobile

import com.isel.leic.ps.ion_classcode.domain.PendingTeacher
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.http.*
import com.isel.leic.ps.ion_classcode.http.controllers.web.*
import com.isel.leic.ps.ion_classcode.http.model.output.*
import com.isel.leic.ps.ion_classcode.services.UserServices
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.services.*
import com.isel.leic.ps.ion_classcode.utils.Result
import com.isel.leic.ps.ion_classcode.utils.cypher.AESEncrypt
import okhttp3.internal.EMPTY_REQUEST
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * Teacher authentication with the respective scope.
 */
@RestController
class AuthControllerMobile(
    private val userServices: UserServices,
    private val githubServices: GithubServices,
) {
    /**
     * Teacher authentication with the respective scope.
     */
    @GetMapping(Uris.MOBILE_AUTH_PATH, produces = ["application/vnd.siren+json"])
    fun auth(): ResponseEntity<*> {
        val state = generateUserState()
        return siren(
            value = AuthRedirect(
                url = "$GITHUB_BASE_URL${GITHUB_OAUTH_URI(MOBILE_GITHUB_TEACHER_SCOPE, state.value)}",
            ),
            headers = HttpHeaders().apply {
                add(HttpHeaders.SET_COOKIE, state.cookie.toString())
            },
        ) {
            clazz("auth")
            link(rel = LinkRelation("self"), href = Uris.MOBILE_AUTH_PATH)
        }
    }

    data class LoginResponse(
        val tokenInfo: ClientToken,
    ) : OutputModel

    /**
     * Callback from the OAuth2 provider.
     * It fetches the access token and the user info.
     * Check if the user is created and verified, and computes accordingly.
     */
    @GetMapping("/api/auth/callback/mobile", produces = ["application/json"])
    suspend fun callback(
        @RequestParam code: String,
        @RequestParam state: String,
        @CookieValue userState: String,
    ): ResponseEntity<*> {
        if (state != userState) {
            return ResponseEntity
                .status(Status.REDIRECT)
                .header(HttpHeaders.LOCATION, "http://localhost:3000/auth/error/callback")
                .body(EMPTY_REQUEST)
        }
        val accessToken = githubServices.fetchAccessToken(code = code, isMobile = false)
        val userGithubInfo = githubServices.fetchUserInfo(accessToken.access_token)
        return when (val userInfo = userServices.getUserByGithubId(userGithubInfo.id)) {
            is Result.Success -> {
                if (userInfo.value.isCreated) {
                    when (userInfo.value) {
                        is Teacher -> {
                            val cookie = generateSessionCookie(userInfo.value.token)
                            ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .header(HttpHeaders.LOCATION, "classcode://callback")
                                .body(LoginResponse(tokenInfo = accessToken))
                        }

                        else -> {
                            ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.LOCATION, "classcode://callback/student")
                                .body(EMPTY_REQUEST)
                        }
                    }
                } else {
                    when (userInfo.value) {
                        is PendingTeacher -> {
                            ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.LOCATION, "classcode://callback/pendingTeacher")
                                .body(EMPTY_REQUEST)
                        }

                        else -> {
                            ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.LOCATION, "classcode://callback/pendingStudent")
                                .body(EMPTY_REQUEST)
                        }
                    }
                }
            }
            is Result.Problem ->
                ResponseEntity
                    .status(Status.REDIRECT)
                    .header(HttpHeaders.LOCATION, "classcode://callback?error")
                    .body(EMPTY_REQUEST)
        }
    }
}

// TODO("Didn´t use the other generate user state because of possible merge conflicts. Then change to use the same")
private fun generateUserState(): OAuthState {
    val state = UUID.randomUUID().toString()
    val cookie = ResponseCookie.from(STATE_COOKIE_NAME, state)
        .path(STATE_COOKIE_PATH)
        .maxAge(HALF_HOUR)
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .build()

    return OAuthState(state, cookie)
}

private fun generateSessionCookie(token: String): ResponseCookie {
    return ResponseCookie.from(AUTHORIZATION_COOKIE_NAME, AESEncrypt.encrypt(token))
        .httpOnly(true)
        .sameSite("Strict")
        .secure(true)
        .maxAge(FULL_DAY)
        .path("/api/mobile")
        .build()
}
