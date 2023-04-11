package com.isel.leic.ps.ion_classcode.sketches

import com.isel.leic.ps.ion_classcode.http.GITHUB_API_BASE_URL
import com.isel.leic.ps.ion_classcode.http.GITHUB_ORG_CREATE_REPO_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_ORG_REPOS_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_ORG_TEAMS_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_ORG_TEAMS_USER_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_ORG_TEAM_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_ORG_USER_URI
import com.isel.leic.ps.ion_classcode.http.OkHttp
import com.isel.leic.ps.ion_classcode.http.controllers.web.ORG_NAME
import com.isel.leic.ps.ion_classcode.http.makeCallToList
import com.isel.leic.ps.ion_classcode.http.makeCallToObject
import com.isel.leic.ps.ion_classcode.http.model.github.GithubRepo
import com.isel.leic.ps.ion_classcode.http.model.github.OrgMembership
import com.isel.leic.ps.ion_classcode.http.model.github.OrgRepoCreated
import com.isel.leic.ps.ion_classcode.http.model.github.TeamAddUser
import com.isel.leic.ps.ion_classcode.http.model.github.TeamCreated
import com.isel.leic.ps.ion_classcode.http.model.github.TeamList
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/** Finding place to this functions **/

class GithubAPIFuncions(
    private val okHttp: OkHttp
) {
    private suspend fun getOrgRepos(
        orgName: String = ORG_NAME,
        perPage: Int = 100,
        page: Int = 1,
        accessToken: String
    ): List<GithubRepo> {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_REPOS_URI(orgName, perPage, page)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .build()

        return okHttp.makeCallToList(request)
    }

    private suspend fun addUserToOrg(orgName: String, userName: String, accessToken: String): OrgMembership {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_USER_URI(orgName, userName)}")
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
            .post(
                "{\"name\": \"$repoName\",\"description\": \"This is your first repository\",\"homepage\": \"https://github.com\",\"private\": true,\"has_issues\": true,\"has_projects\": true,\"has_wiki\": true }".toRequestBody(
                    "application/json".toMediaType()
                )
            )
            .build()

        return okHttp.makeCallToList(request)
    }

    private suspend fun createOrgTeam(
        orgName: String,
        teamName: String,
        description: String,
        accessToken: String
    ): TeamCreated {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_TEAMS_URI(orgName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .post(
                "{\"name\":\"$teamName\",\"$description\":\"description\",\"permission\":\"push\",\"privacy\":\"secret\"}".toRequestBody(
                    "application/json".toMediaType()
                )
            )
            .build()

        return okHttp.makeCallToObject(request)
    }

    private suspend fun addUserToTeam(
        orgName: String,
        teamName: String,
        userName: String,
        accessToken: String
    ): TeamAddUser {
        val request =
            Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_TEAMS_USER_URI(orgName, teamName, userName)}")
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Accept", "application/json")
                .put("{\"role\":\"member\"}".toRequestBody("application/json".toMediaType()))
                .build()

        return okHttp.makeCallToObject(request)
    }

    // Don´t have a response body. Status is 204
    private suspend fun removeUserOfTeam(orgName: String, teamName: String, userName: String, accessToken: String) {
        val request =
            Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_TEAMS_USER_URI(orgName, teamName, userName)}")
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Accept", "application/json")
                .delete()
                .build()

        return okHttp.makeCallToObject(request)
    }

    private suspend fun addRepoToTeam(
        orgName: String,
        teamName: String,
        prefix: String,
        teamId: Int,
        accessToken: String
    ): List<OrgRepoCreated> {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_CREATE_REPO_URI(orgName)}")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .post(
                "{\"name\": \"$prefix-$teamName\",\"description\": \"This is your first repository\",\"homepage\": \"https://github.com\",\"private\": true,\"has_issues\": true,\"has_projects\": true,\"has_wiki\": true,\"team_id\": $teamId }".toRequestBody(
                    "application/json".toMediaType()
                )
            )
            .build()

        return okHttp.makeCallToObject(request)
    }

    // Don´t have a response body. Status is 204
    private suspend fun deleteOrgTeam(orgName: String, teamName: String, accessToken: String) {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL${GITHUB_ORG_TEAM_URI(orgName, teamName)}")
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
