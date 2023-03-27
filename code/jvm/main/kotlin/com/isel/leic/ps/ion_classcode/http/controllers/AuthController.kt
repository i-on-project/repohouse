package com.isel.leic.ps.ion_classcode.http.controllers

import com.isel.leic.ps.ion_classcode.InvalidAuthenticationStateException
import com.isel.leic.ps.ion_classcode.http.GITHUB_ACCESS_TOKEN_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_API_BASE_URL
import com.isel.leic.ps.ion_classcode.http.GITHUB_BASE_URL
import com.isel.leic.ps.ion_classcode.http.GITHUB_OAUTH_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_ORG_CREATE_REPO_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_ORG_REPOS_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_ORG_TEAMS_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_ORG_TEAMS_USER_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_ORG_TEAM_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_ORG_USER_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_USERINFO_URI
import com.isel.leic.ps.ion_classcode.http.OkHttp
import com.isel.leic.ps.ion_classcode.http.Status
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.makeCallToList
import com.isel.leic.ps.ion_classcode.http.makeCallToObject
import com.isel.leic.ps.ion_classcode.http.model.output.ClientToken
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubUserInfo
import com.isel.leic.ps.ion_classcode.http.model.output.GithubResponses.GithubRepo
import com.isel.leic.ps.ion_classcode.http.model.output.GithubResponses.OrgMembership
import com.isel.leic.ps.ion_classcode.http.model.output.GithubResponses.OrgRepoCreated
import com.isel.leic.ps.ion_classcode.http.model.output.GithubResponses.TeamAddUser
import com.isel.leic.ps.ion_classcode.http.model.output.GithubResponses.TeamCreated
import com.isel.leic.ps.ion_classcode.http.model.output.GithubResponses.TeamList
import com.isel.leic.ps.ion_classcode.http.model.output.OAuthState
import com.isel.leic.ps.ion_classcode.http.services.UserServices
import com.isel.leic.ps.ion_classcode.infra.SirenModel
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.utils.Either
import jakarta.servlet.http.HttpServletResponse
import java.util.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

const val ORG_NAME = "test-project-isel"
const val GITHUB_TEACHER_SCOPE = "admin:org"
const val GITHUB_STUDENT_SCOPE = "repo"

const val STATE_COOKIE_NAME = "userState"
const val STATE_COOKIE_PATH = Uris.CALLBACK_PATH
const val HALF_HOUR: Long = 60 * 30
const val APP_COOKIE_NAME = "Session"

@RestController
class AuthController(
    private val okHttp: OkHttp,
    private val userServices: UserServices
) {

    @GetMapping(Uris.AUTH_TEACHER_PATH)
    fun authTeacher(): ResponseEntity<Any> {
        val state = generateUserState()
        return ResponseEntity
            .status(Status.REDIRECT)
            .header(HttpHeaders.SET_COOKIE, state.cookie.toString())
            .header(HttpHeaders.LOCATION, "$GITHUB_BASE_URL${GITHUB_OAUTH_URI(GITHUB_TEACHER_SCOPE, state.value)}")
            .build()
    }

    @GetMapping(Uris.AUTH_STUDENT_PATH)
    fun authStudent(): ResponseEntity<Any> {
        val state = generateUserState()
        return ResponseEntity
            .status(Status.REDIRECT)
            .header(HttpHeaders.SET_COOKIE, state.cookie.toString())
            .header(HttpHeaders.LOCATION, "$GITHUB_BASE_URL${GITHUB_OAUTH_URI(GITHUB_STUDENT_SCOPE, state.value)}")
            .build()
    }

    @GetMapping(Uris.CALLBACK_PATH)
    suspend fun callback(
        @RequestParam code: String,
        @RequestParam state: String,
        @CookieValue userState: String,
        response: HttpServletResponse
    ): SirenModel<*> {
        if (state != userState) throw InvalidAuthenticationStateException()
        val accessToken = fetchAccessToken(code)
        val userInfo = fetchUserInfo(accessToken.access_token)
        val user = userServices.getUserByGithubId(userInfo.id)

        return when(user) {
            is Either.Right -> {
                // TODO(Check if user is created and verified)
                // TODO(If not, redirect to verification page)
                // TODO(If yes, create a session, and redirect to menu page)
                val cookie = ResponseCookie.from(APP_COOKIE_NAME, /** TODO(Encrypt token) **/ )
                    .httpOnly(true)
                    .sameSite("Strict")
                    .secure(true)
                    .maxAge(60*60*24)
                    .path("/api")
                    .build()
                response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString())
                siren("Dummy"){}
            }
            is Either.Left -> {
                // TODO(Store new user info in database)
                // TODO(Redirect for verification page)
                siren("Dummy"){}
            }
        }
    }

    @GetMapping(Uris.LOGOUT)
    fun logout(
        response: HttpServletResponse
    ): ResponseEntity<Any> {
        val cookie = ResponseCookie.from(APP_COOKIE_NAME, "")
            .httpOnly(true)
            .sameSite("Strict")
            .secure(true)
            .maxAge(0)
            .path("/api")
            .build()
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString())
        return ResponseEntity
            .status(Status.REDIRECT)
            .header(HttpHeaders.LOCATION, Uris.HOME)
            .build()
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

        return okHttp.makeCallToObject(request)
    }

    private suspend fun fetchUserInfo(accessToken: String): GitHubUserInfo {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL$GITHUB_USERINFO_URI")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .build()

        return okHttp.makeCallToObject(request)
    }

    /** Finding place to this functions **/

    private suspend fun getOrgRepos(orgName: String = ORG_NAME, perPage: Int = 100, page: Int = 1, accessToken: String): List<GithubRepo> {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_REPOS_URI(orgName,perPage, page)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .build()

        return okHttp.makeCallToList(request)
    }

    private suspend fun addUserToOrg(orgName: String, userName: String, accessToken: String): OrgMembership {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_USER_URI(orgName,userName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .put("{\"role\":\"member\"}".toRequestBody("application/json".toMediaType()))
            .build()

        return okHttp.makeCallToObject(request)
    }

    private suspend fun listOrgTeams(orgName: String, accessToken: String): List<TeamList> {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_TEAMS_URI(orgName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .get()
            .build()

        return okHttp.makeCallToList(request)
    }

    private suspend fun createOrgRepo(orgName: String, repoName: String, accessToken: String): List<OrgRepoCreated> {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_CREATE_REPO_URI(orgName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .post("{\"name\": \"$repoName\",\"description\": \"This is your first repository\",\"homepage\": \"https://github.com\",\"private\": true,\"has_issues\": true,\"has_projects\": true,\"has_wiki\": true }".toRequestBody("application/json".toMediaType()))
            .build()

        return okHttp.makeCallToList(request)
    }

    private suspend fun createOrgTeam(orgName: String, teamName: String, description: String, accessToken: String): TeamCreated {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_TEAMS_URI(orgName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .post("{\"name\":\"$teamName\",\"$description\":\"description\",\"permission\":\"push\",\"privacy\":\"secret\"}".toRequestBody("application/json".toMediaType()))
            .build()

        return okHttp.makeCallToObject(request)
    }

    private suspend fun addUserToTeam(orgName: String, teamName: String, userName: String, accessToken: String): TeamAddUser {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_TEAMS_USER_URI(orgName,teamName,userName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .put("{\"role\":\"member\"}".toRequestBody("application/json".toMediaType()))
            .build()

        return okHttp.makeCallToObject(request)
    }

    // Don´t have a response body. Status is 204
    private suspend fun removeUserOfTeam(orgName: String, teamName: String, userName: String, accessToken: String) {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_TEAMS_USER_URI(orgName,teamName,userName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .delete()
            .build()

        return okHttp.makeCallToObject(request)
    }

    private suspend fun addRepoToTeam(orgName: String, teamName: String, prefix: String, teamId: Int, accessToken: String): List<OrgRepoCreated> {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_CREATE_REPO_URI(orgName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .post("{\"name\": \"$prefix-$teamName\",\"description\": \"This is your first repository\",\"homepage\": \"https://github.com\",\"private\": true,\"has_issues\": true,\"has_projects\": true,\"has_wiki\": true,\"team_id\": $teamId }".toRequestBody("application/json".toMediaType()))
            .build()

        return okHttp.makeCallToObject(request)
    }

    // Don´t have a response body. Status is 204
    private suspend fun deleteOrgTeam(orgName: String, teamName: String, accessToken: String) {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_TEAM_URI(orgName,teamName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .delete()
            .build()

        return okHttp.makeCallToObject(request)
    }

    private suspend fun checkScopes(accessToken: String): String {
        val request = Request.Builder().url(" https://api.github.com/users/codertocat")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .build()

        return okHttp.makeCallToObject(request)
    }

}

