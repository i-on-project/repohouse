package com.isel.leic.ps.ionClassCode.http.controllers.mobile

import com.isel.leic.ps.ionClassCode.domain.Teacher
import com.isel.leic.ps.ionClassCode.domain.input.ChallengeInput
import com.isel.leic.ps.ionClassCode.http.GITHUB_BASE_URL
import com.isel.leic.ps.ionClassCode.http.MOBILE_GITHUB_OAUTH_URI
import com.isel.leic.ps.ionClassCode.http.Status
import com.isel.leic.ps.ionClassCode.http.Uris
import com.isel.leic.ps.ionClassCode.http.controllers.web.AUTHORIZATION_COOKIE_NAME
import com.isel.leic.ps.ionClassCode.http.controllers.web.FULL_DAY
import com.isel.leic.ps.ionClassCode.http.controllers.web.HALF_HOUR
import com.isel.leic.ps.ionClassCode.http.controllers.web.MOBILE_GITHUB_TEACHER_SCOPE
import com.isel.leic.ps.ionClassCode.http.controllers.web.STATE_COOKIE_NAME
import com.isel.leic.ps.ionClassCode.http.controllers.web.STATE_COOKIE_PATH
import com.isel.leic.ps.ionClassCode.http.model.output.OAuthState
import com.isel.leic.ps.ionClassCode.http.model.output.OutputModel
import com.isel.leic.ps.ionClassCode.http.model.problem.Problem
import com.isel.leic.ps.ionClassCode.infra.LinkRelation
import com.isel.leic.ps.ionClassCode.infra.siren
import com.isel.leic.ps.ionClassCode.services.GithubServices
import com.isel.leic.ps.ionClassCode.services.UserServices
import com.isel.leic.ps.ionClassCode.utils.Result
import com.isel.leic.ps.ionClassCode.utils.cypher.AESEncrypt
import okhttp3.internal.EMPTY_REQUEST
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
    @GetMapping(Uris.MOBILE_AUTH_PATH)
    fun auth(
        @RequestParam challengeMethod: String,
        @RequestParam challenge: String,
    ): ResponseEntity<*> {
        val state = generateUserState()
        userServices.storeChallengeInfo(challengeMethod = challengeMethod, challenge = challenge, state = state.value)
        return ResponseEntity
            .status(Status.REDIRECT)
            .header(HttpHeaders.SET_COOKIE, state.cookie.toString())
            .header(HttpHeaders.LOCATION, "$GITHUB_BASE_URL${MOBILE_GITHUB_OAUTH_URI(MOBILE_GITHUB_TEACHER_SCOPE, state.value)}")
            .body(EMPTY_REQUEST)
    }

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
                .header(HttpHeaders.LOCATION, "classcode://callback/error")
                .body(EMPTY_REQUEST)
        }
        return ResponseEntity
            .status(Status.REDIRECT)
            .header(HttpHeaders.LOCATION, "classcode://callback?code=$code&state=$state")
            .body(EMPTY_REQUEST)
    }
    data class AccessTokenResponse(val accessToken: String) : OutputModel

    @PostMapping(Uris.MOBILE_GET_ACCESS_TOKEN_PATH)
    suspend fun getAccessToken(
        @RequestBody challengeInfo: ChallengeInput,
    ): ResponseEntity<*> {
        when (val res = userServices.verifySecret(secret = challengeInfo.secret, state = challengeInfo.state)) {
            is Result.Problem -> {
                return userServices.problem(error = res.value)
            }
            is Result.Success -> {
                val accessToken = githubServices.fetchAccessToken(code = challengeInfo.code)
                val userGithubInfo = githubServices.fetchUserInfo(accessToken = accessToken.access_token)
                return when (val userInfo = userServices.getUserByGithubId(githubId = userGithubInfo.id)) {
                    is Result.Success -> {
                        if (userInfo.value.isCreated) {
                            when (userInfo.value) {
                                is Teacher -> {
                                    val cookie = generateSessionCookie(userInfo.value.token)
                                    siren(
                                        value = AccessTokenResponse(accessToken = accessToken.access_token),
                                        headers = HttpHeaders().apply {
                                            add(HttpHeaders.SET_COOKIE, cookie.toString())
                                        },
                                    ) {
                                        clazz("accessToken")
                                        link(rel = LinkRelation("self"), href = Uris.MOBILE_GET_ACCESS_TOKEN_PATH)
                                    }
                                }
                                else -> {
                                    Problem.unauthorized
                                }
                            }
                        } else {
                            Problem.unauthorized
                        }
                    }
                    is Result.Problem -> {
                        Problem.userNotFound
                    }
                }
            }
        }
    }
}
private fun generateUserState(): OAuthState {
    val state = UUID.randomUUID().toString()
    val cookie = ResponseCookie.from(STATE_COOKIE_NAME, state)
        .path(STATE_COOKIE_PATH)
        .maxAge(HALF_HOUR)
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .build()

    return OAuthState(value = state, cookie = cookie)
}

private fun generateSessionCookie(token: String): ResponseCookie {
    return ResponseCookie.from(AUTHORIZATION_COOKIE_NAME, AESEncrypt.encrypt(stringToEncrypt = token))
        .httpOnly(true)
        .sameSite("Strict")
        .secure(true)
        .maxAge(FULL_DAY)
        .path("/api/mobile")
        .build()
}
