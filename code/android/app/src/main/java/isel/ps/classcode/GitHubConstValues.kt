package isel.ps.classcode

import okhttp3.MediaType.Companion.toMediaType

/**
 * Constants used in the requests to GitHub API
 */

val MEDIA_TYPE = "application/vnd.siren+json".toMediaType()
const val GITHUB_API_BASE_URL = "https://api.github.com"
const val GITHUB_USERINFO_URI = "/user"

val GITHUB_ADD_TEAM: (String) -> String = { orgName ->
    "$GITHUB_API_BASE_URL/orgs/$orgName/teams"
}

val GITHUB_CREATE_REPO: (String) -> String = { orgName ->
    "$GITHUB_API_BASE_URL/orgs/$orgName/repos"
}

val GITHUB_ADD_MEMBER_TO_TEAM: (String, String, String) -> String = { orgName, teamSlug, username ->
    "${GITHUB_ADD_TEAM(orgName)}/$teamSlug/memberships/$username"
}

val GITHUB_REMOVE_MEMBER_FROM_TEAM: (String, String, String) -> String = { orgName, teamSlug, username ->
    "${GITHUB_ADD_TEAM(orgName)}/$teamSlug/memberships/$username"
}
