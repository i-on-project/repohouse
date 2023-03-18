package com.isel.leic.ps.ion_repohouse.http.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.isel.leic.ps.ion_repohouse.InvalidAuthenticationStateException
import com.isel.leic.ps.ion_repohouse.http.Status
import com.isel.leic.ps.ion_repohouse.http.Uris
import com.isel.leic.ps.ion_repohouse.http.model.output.ClientToken
import com.isel.leic.ps.ion_repohouse.http.model.output.GitHubUserInfo
import com.isel.leic.ps.ion_repohouse.http.model.output.HomeOutputModel
import com.isel.leic.ps.ion_repohouse.http.model.output.OAuthState
import com.isel.leic.ps.ion_repohouse.http.model.output.GithubResponses.GithubRepo
import com.isel.leic.ps.ion_repohouse.http.model.output.GithubResponses.OrgMembership
import com.isel.leic.ps.ion_repohouse.http.model.output.GithubResponses.OrgRepoCreated
import com.isel.leic.ps.ion_repohouse.http.model.output.GithubResponses.TeamAddUser
import com.isel.leic.ps.ion_repohouse.http.model.output.GithubResponses.TeamCreated
import com.isel.leic.ps.ion_repohouse.http.model.output.GithubResponses.TeamList
import com.isel.leic.ps.ion_repohouse.http.send
import com.isel.leic.ps.ion_repohouse.infra.LinkRelation
import com.isel.leic.ps.ion_repohouse.infra.SirenModel
import com.isel.leic.ps.ion_repohouse.infra.siren
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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

// GitHub constants
const val GITHUB_BASE_URL = "https://github.com"
const val GITHUB_API_BASE_URL = "https://api.github.com"
const val GITHUB_OAUTH_URI = "$GITHUB_BASE_URL/login/oauth/authorize"
const val GITHUB_USERINFO_URI = "/user"
val GITHUB_ACCESS_TOKEN_URI: (code: String) -> String = { code ->
    "/login/oauth/access_token?" +
        "client_id=" + System.getenv("GITHUB_CLIENT_ID") +
        "&client_secret=" + System.getenv("GITHUB_CLIENT_SECRET") +
        "&code=" + code
}
val GITHUB_ORG_REPOS: (orgName: String, perPage: Int, page: Int) -> String = { org, perPage, page -> "/orgs/$org/repos?type=all" }
val GITHUB_ORG_CREATE_REPO: (orgName: String) -> String = { org -> "/orgs/$org/repos" }
val GITHUB_ORG_TEAMS: (orgName: String) -> String = { org -> "/orgs/$org/teams" }
val GITHUB_ORG_TEAM: (orgName: String, teamName: String) -> String = { org, team -> "/orgs/$org/teams/$team" }
val GITHUB_ORG_TEAMS_USER: (orgName: String, teamName: String, userName: String) -> String = { org, team, user -> "/orgs/$org/teams/$team/memberships/$user" }
val GITHUB_ORG_USER: (orgName: String, userName: String) -> String = { org, user -> "/orgs/$org/memberships/$user" }
val ORG_NAME = "test-project-isel"
const val GITHUB_TEACHER_SCOPE = "admin:org"
const val GITHUB_STUDENT_SCOPE = "repo"

// Cookie constants
const val STATE_COOKIE_NAME = "userState"
const val STATE_COOKIE_PATH = Uris.CALLBACK_PATH
const val HALF_HOUR: Long = 60 * 30

@RestController
class AuthController(
    val httpClient: OkHttpClient,
    val jsonMapper: ObjectMapper,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(AuthController::class.java)
    }

    @GetMapping(Uris.HOME)
    fun home() = siren(value = HomeOutputModel()) {
        link(rel = LinkRelation("self"), href = Uris.homeUri())
        link(rel = LinkRelation("auth"), href = Uris.authUri())
        link(rel = LinkRelation("menu"), href = Uris.menuUri(), clazz = listOf("Authenticated"))
    }

    @GetMapping(Uris.AUTH_PATH)
    fun auth(): ResponseEntity<Any> {
        val state = generateUserState()
        return ResponseEntity
            .status(Status.REDIRECT)
            .header(HttpHeaders.SET_COOKIE, state.cookie.toString())
            .header(
                HttpHeaders.LOCATION,
                GITHUB_OAUTH_URI +
                    "?client_id=" + System.getenv("GITHUB_CLIENT_ID") +
                    "&scope=" + GITHUB_TEACHER_SCOPE +
                    "&state=" + state.value,
            ).build()
    }

    @GetMapping(Uris.CALLBACK_PATH)
    suspend fun callback(
        @RequestParam code: String,
        @RequestParam state: String,
        @CookieValue userState: String,
    ): SirenModel<List<OrgRepoCreated>> {
        if (state != userState) throw InvalidAuthenticationStateException()
        val accessToken = fetchAccessToken(code)
        // val userInfo = fetchUserInfo(accessToken.access_token)
        val orgList = createOrgRepo(ORG_NAME, "repo", accessToken = accessToken.access_token)
        // TODO(Store user info in database and create a session)
        return siren(
            value = orgList,
        ) {
            clazz("Auth")
            link(rel = LinkRelation("self"), href = Uris.authUri())
            link(rel = LinkRelation("home"), href = Uris.homeUri())
            link(rel = LinkRelation("menu"), href = Uris.menuUri(), clazz = listOf("Authenticated"))
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

    /** Needing to be tested **/

    private suspend fun getOrgRepos(orgName: String = ORG_NAME, perPage: Int = 100, page: Int = 1, accessToken: String): List<GithubRepo> {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_REPOS(orgName,perPage, page)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .build()

        return request.send(httpClient, jsonMapper)
    }

    private suspend fun addUserToOrg(orgName: String, userName: String, accessToken: String): OrgMembership {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_USER(orgName,userName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .put("{\"role\":\"member\"}".toRequestBody("application/json".toMediaType()))
            .build()

        return request.send(httpClient, jsonMapper)
    }

    private suspend fun listOrgTeams(orgName: String, accessToken: String): List<TeamList> {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_TEAMS(orgName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .get()
            .build()

        return request.send(httpClient, jsonMapper)
    }

    private suspend fun createOrgRepo(orgName: String, repoName: String, accessToken: String): List<OrgRepoCreated> {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_CREATE_REPO(orgName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .post("{\"name\": \"$repoName\",\"description\": \"This is your first repository\",\"homepage\": \"https://github.com\",\"private\": true,\"has_issues\": true,\"has_projects\": true,\"has_wiki\": true }".toRequestBody("application/json".toMediaType()))
            .build()

        return request.send(httpClient, jsonMapper)
    }

    private suspend fun createOrgTeam(orgName: String, teamName: String, description: String, accessToken: String): TeamCreated {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_TEAMS(orgName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .post("{\"name\":\"$teamName\",\"$description\":\"description\",\"permission\":\"push\",\"privacy\":\"secret\"}".toRequestBody("application/json".toMediaType()))
            .build()

        return request.send(httpClient, jsonMapper)
    }

    private suspend fun addUserToTeam(orgName: String, teamName: String, userName: String, accessToken: String): TeamAddUser {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_TEAMS_USER(orgName,teamName,userName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .put("{\"role\":\"member\"}".toRequestBody("application/json".toMediaType()))
            .build()

        return request.send(httpClient, jsonMapper)
    }

    // Don´t have a response body. Status is 204
    private suspend fun removeUserOfTeam(orgName: String, teamName: String, userName: String, accessToken: String) {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_TEAMS_USER(orgName,teamName,userName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .delete()
            .build()

        return request.send(httpClient, jsonMapper)
    }

    private suspend fun addRepoToTeam(orgName: String, teamName: String, prefix: String, teamId: Int, accessToken: String): List<OrgRepoCreated> {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_CREATE_REPO(orgName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .post("{\"name\": \"$prefix-$teamName\",\"description\": \"This is your first repository\",\"homepage\": \"https://github.com\",\"private\": true,\"has_issues\": true,\"has_projects\": true,\"has_wiki\": true,\"team_id\": $teamId }".toRequestBody("application/json".toMediaType()))
            .build()

        return request.send(httpClient, jsonMapper)
    }

    // Don´t have a response body. Status is 204
    private suspend fun deleteOrgTeam(orgName: String, teamName: String, accessToken: String) {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_TEAM(orgName,teamName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .delete()
            .build()

        return request.send(httpClient, jsonMapper)
    }

    private suspend fun checkScopes(accessToken: String): String {
        val request = Request.Builder().url(" https://api.github.com/users/codertocat")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .build()

        return request.send(httpClient, jsonMapper)
    }
}
