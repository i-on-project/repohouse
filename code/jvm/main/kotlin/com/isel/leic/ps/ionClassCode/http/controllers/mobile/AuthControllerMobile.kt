package com.isel.leic.ps.ionClassCode.http.controllers.mobile

import com.isel.leic.ps.ionClassCode.domain.PendingTeacher
import com.isel.leic.ps.ionClassCode.domain.Teacher
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
import com.isel.leic.ps.ionClassCode.infra.LinkRelation
import com.isel.leic.ps.ionClassCode.infra.siren
import com.isel.leic.ps.ionClassCode.services.GithubServices
import com.isel.leic.ps.ionClassCode.services.UserServices
import com.isel.leic.ps.ionClassCode.utils.Result
import com.isel.leic.ps.ionClassCode.utils.cypher.AESDecrypt
import com.isel.leic.ps.ionClassCode.utils.cypher.AESEncrypt
import okhttp3.internal.EMPTY_REQUEST
import org.slf4j.LoggerFactory
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
    @GetMapping(Uris.MOBILE_AUTH_PATH)
    fun auth(): ResponseEntity<*> {
        val state = generateUserState()
        return ResponseEntity
            .status(Status.REDIRECT)
            .header(HttpHeaders.SET_COOKIE, state.cookie.toString())
            .header(HttpHeaders.LOCATION, "$GITHUB_BASE_URL${MOBILE_GITHUB_OAUTH_URI(MOBILE_GITHUB_TEACHER_SCOPE, state.value)}")
            .body(EMPTY_REQUEST)
    }

    @GetMapping(Uris.MOBILE_GET_ACCESS_TOKEN_PATH)
    fun getAccessToken(
        @RequestParam code: String,
        @RequestParam githubId: Long,
    ): ResponseEntity<*> {
        return when (val tokens = userServices.getTokens(githubId = githubId)) {
            is Result.Success -> {
                val decryptedToken = AESDecrypt.decryptAccessToken(accessToken = tokens.value.accessToken, code = code)
                val cookie = generateSessionCookie(tokens.value.classCodeToken)
                siren(
                    value = AccessTokenResponse(accessToken = decryptedToken),
                    headers = HttpHeaders().apply {
                        add(HttpHeaders.SET_COOKIE, cookie.toString())
                    },
                ) {
                    clazz("accessToken")
                    link(rel = LinkRelation("self"), href = Uris.MOBILE_GET_ACCESS_TOKEN_PATH)
                }
            }
            is Result.Problem -> {
                userServices.problem(error = tokens.value)
            }
        }
    }

    data class AccessTokenResponse(val accessToken: String) : OutputModel

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
        val accessToken = githubServices.fetchAccessToken(code = code)
        val userGithubInfo = githubServices.fetchUserInfo(accessToken = accessToken.access_token)
        logger.info("User info: $userGithubInfo")
        return when (val userInfo = userServices.getUserByGithubId(githubId = userGithubInfo.id)) {
            is Result.Success -> {
                if (userInfo.value.isCreated) {
                    when (userInfo.value) {
                        is Teacher -> {
                            val encryptedToken = AESEncrypt.encryptAccessToken(accessToken = accessToken.access_token, code = code)
                            userServices.storeAccessTokenEncrypted(token = encryptedToken, githubId = userInfo.value.githubId)
                            ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.LOCATION, "classcode://callback?code=$code&github_id=${userInfo.value.githubId}")
                                .body(EMPTY_REQUEST)
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
                    .header(HttpHeaders.LOCATION, "classcode://callback/user_not_present/")
                    .body(EMPTY_REQUEST)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthControllerMobile::class.java)
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
