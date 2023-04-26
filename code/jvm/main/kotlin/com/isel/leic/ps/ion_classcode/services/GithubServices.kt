package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.http.GITHUB_API_BASE_URL
import com.isel.leic.ps.ion_classcode.http.GITHUB_BASE_URL
import com.isel.leic.ps.ion_classcode.http.GITHUB_USERINFO_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_USERMAILS_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_USER_ORGS
import com.isel.leic.ps.ion_classcode.http.GITHUB_ACCESS_TOKEN_URI
import com.isel.leic.ps.ion_classcode.http.OkHttp
import com.isel.leic.ps.ion_classcode.http.makeCallToList
import com.isel.leic.ps.ion_classcode.http.makeCallToObject
import com.isel.leic.ps.ion_classcode.http.model.github.Collaborator
import com.isel.leic.ps.ion_classcode.http.model.github.Commit
import com.isel.leic.ps.ion_classcode.http.model.github.RepoOrg
import com.isel.leic.ps.ion_classcode.http.model.github.RepoReponse
import com.isel.leic.ps.ion_classcode.http.model.github.Tag
import com.isel.leic.ps.ion_classcode.http.model.github.Tags
import com.isel.leic.ps.ion_classcode.http.model.output.ClientToken
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubOrgsModel
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubUserEmail
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubUserInfo
import okhttp3.Request
import okhttp3.internal.EMPTY_REQUEST
import org.springframework.stereotype.Component
import java.sql.Timestamp

/**
 * Service to get the repositories from GitHub
 */
@Component
class GithubServices(
    val okHttp: OkHttp,
) {

    /**
     * Method to fetch the user access token from GitHub.
     */
    suspend fun fetchAccessToken(code: String): ClientToken {
        val request = Request.Builder().url("$GITHUB_BASE_URL${GITHUB_ACCESS_TOKEN_URI(code)}")
            .addHeader("Accept", "application/json")
            .post(EMPTY_REQUEST)
            .build()

        return okHttp.makeCallToObject(request)
    }

    /**
     * Method to fetch the user info from GitHub.
     */
    suspend fun fetchUserInfo(accessToken: String): GitHubUserInfo {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL$GITHUB_USERINFO_URI")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .build()

        return okHttp.makeCallToObject(request)
    }

    /**
     * Method to fetch the user emails from GitHub.
     */
    suspend fun fetchUserEmails(accessToken: String): List<GitHubUserEmail> {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL$GITHUB_USERMAILS_URI")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/vnd.github+json")
            .build()

        return okHttp.makeCallToList(request)
    }

    /**
     * Method to fetch the teacher orgs from GitHub.
     */
    suspend fun fetchTeacherOrgs(githubToken: String): List<GitHubOrgsModel> {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL$GITHUB_USER_ORGS")
            .addHeader("Authorization", "Bearer $githubToken")
            .addHeader("Accept", "application/vnd.github+json")
            .build()

        return okHttp.makeCallToList(request)
    }

    suspend fun getUserOrgs(token: String):List<GitHubOrgsModel>{
        val orgsRequest = Request.Builder().url("")
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept", "application/json")
            .build()

        return okHttp.makeCallToList<GitHubOrgsModel>(orgsRequest)
    }

    /**
     * Method to get the repositories from GitHub based on the organization and respective repository name
     */
    suspend fun getRepository(repoName: String, token: String, orgName: String): RepoReponse {
        val tagsList = mutableListOf<Tag>()

        val requestOrg = Request.Builder().url("$GITHUB_API_BASE_URL/repos/$orgName/$repoName")
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept", "application/json")
            .build()

        val orgRepo = okHttp.makeCallToObject<RepoOrg>(requestOrg)

        val requestCollaborators = Request.Builder().url("$GITHUB_API_BASE_URL/repos/$orgName/$repoName/collaborators")
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept", "application/json")
            .build()

        val collaborators = okHttp.makeCallToList<Collaborator>(requestCollaborators)

        val requestTags = Request.Builder().url(orgRepo.tags_url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept", "application/json")
            .build()

        val tags = okHttp.makeCallToList<Tags>(requestTags)

        tags.forEach {
            val requestCommit = Request.Builder().url(it.commit.url)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Accept", "application/json")
                .build()

            val commit = okHttp.makeCallToObject<Commit>(requestCommit)

            tagsList.add(Tag(it.name, convertGithubTime(commit.commit.author.date)))
        }

        return RepoReponse(orgRepo.name, collaborators, tagsList)
    }

    /**
     * Method to convert the time from GitHub to Timestamp
     */
    private fun convertGithubTime(time: String): Timestamp {
        val year = time.substring(0, 4).toInt()
        val month = time.substring(5, 7).toInt()
        val day = time.substring(8, 10).toInt()
        val hour = time.substring(11, 13).toInt()
        val minute = time.substring(14, 16).toInt()
        val second = time.substring(17, 19).toInt()

        return Timestamp.valueOf("$year-$month-$day $hour:$minute:$second")
    }
}
