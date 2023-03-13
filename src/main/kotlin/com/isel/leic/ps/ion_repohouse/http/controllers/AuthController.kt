package com.isel.leic.ps.ion_repohouse.http.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.isel.leic.ps.ion_repohouse.ClientToken
import com.isel.leic.ps.ion_repohouse.GitHubUserInfo
import com.isel.leic.ps.ion_repohouse.InvalidAuthenticationStateException
import com.isel.leic.ps.ion_repohouse.OAuthState
import com.isel.leic.ps.ion_repohouse.http.Status
import com.isel.leic.ps.ion_repohouse.http.Uris
import com.isel.leic.ps.ion_repohouse.http.send
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.EMPTY_REQUEST
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

//GitHub constants
const val GITHUB_BASE_URL = "https://github.com"
const val GITHUB_API_BASE_URL = "https://api.github.com"
const val GITHUB_OAUTH_URI = "$GITHUB_BASE_URL/login/oauth/authorize"
const val GITHUB_USERINFO_URI = "/user"
val GITHUB_ACCESS_TOKEN_URI: (code: String) -> String = { code ->
        "/login/oauth/access_token?" +
        "client_id=" + System.getenv("CLIENT_ID") +
        "&client_secret=" + System.getenv("CLIENT_SECRET") +
        "&code=" + code
}

const val GITHUB_TEACHER_SCOPE = "admin:org"
const val GITHUB_STUDENT_SCOPE = "read:project"

//Cookie constants
const val STATE_COOKIE_NAME = "userState"
const val STATE_COOKIE_PATH = Uris.CALLBACK_PATH
const val HALF_HOUR: Long = 60 * 30


@RestController
class AuthController(
    val httpClient: OkHttpClient,
    val jsonMapper: ObjectMapper,
) {

    @GetMapping("/")
    fun hello() = "<a href=/auth>Use Github Account</a>"

    @GetMapping(Uris.AUTH_PATH)
    fun auth(): ResponseEntity<Any> {
        val state = generateUserState()
        return ResponseEntity
            .status(Status.REDIRECT)
            .header(HttpHeaders.SET_COOKIE, state.cookie.toString())
            .header(
                HttpHeaders.LOCATION,
                GITHUB_OAUTH_URI +
                "?client_id=" + System.getenv("CLIENT_ID") + "&" +
                "scope=" + GITHUB_TEACHER_SCOPE + "&" +
                "state=" + state.value
            ).build()
    }

    @GetMapping(Uris.CALLBACK_PATH)
    suspend fun callback(
        @RequestParam code: String,
        @RequestParam state: String,
        @CookieValue userState: String,
    ): GitHubUserInfo {
        if (state != userState) throw InvalidAuthenticationStateException()
        val accessToken = fetchAccessToken(code)
        val userInfo = fetchUserInfo(accessToken.access_token)
        // TODO
        return userInfo
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

        return OAuthState(state, cookie)
    }

    private suspend fun fetchAccessToken(code: String): ClientToken {

        val request = Request.Builder().url("$GITHUB_BASE_URL${GITHUB_ACCESS_TOKEN_URI(code)}")
            .addHeader("Accept", "application/json")
            .post(EMPTY_REQUEST)
            .build()

        return request.send(httpClient, jsonMapper)
    }

    private suspend fun fetchUserInfo(accessToken: String): GitHubUserInfo {

        val request = Request.Builder().url("$GITHUB_API_BASE_URL$GITHUB_USERINFO_URI")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .build()

        return request.send(httpClient, jsonMapper)
    }
}
