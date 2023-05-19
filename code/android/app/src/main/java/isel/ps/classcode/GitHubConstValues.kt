package isel.ps.classcode

/**
 * Constants used in the requests to GitHub API
 */

const val GITHUB_API_BASE_URL = "https://api.github.com"
const val GITHUB_USERINFO_URI = "/user"

val GITHUB_ADD_TEAM: (String) -> String = { orgName ->
    "$GITHUB_API_BASE_URL/orgs/$orgName/teams"
}
