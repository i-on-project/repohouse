package com.isel.leic.ps.ion_classcode.http

import com.isel.leic.ps.ion_classcode.http.controllers.web.NGROK_URI

const val GITHUB_BASE_URL = "https://github.com"
const val GITHUB_API_BASE_URL = "https://api.github.com"

val GITHUB_OAUTH_URI: (scope: String, state: String) -> String = { scope, state ->
    "/login/oauth/authorize?" +
        "client_id=${System.getenv("GITHUB_CLIENT_ID")}" +
        "&scope=$scope" +
        "&state=$state" +
        "&redirect_uri=$NGROK_URI/api/auth/callback"
}

const val GITHUB_USERINFO_URI = "/user"
const val GITHUB_USERMAILS_URI = "$GITHUB_USERINFO_URI/emails"

val GITHUB_ACCESS_TOKEN_URI: (code: String) -> String = { code ->
    "/login/oauth/access_token?" +
        "client_id=" + System.getenv("GITHUB_CLIENT_ID") +
        "&client_secret=" + System.getenv("GITHUB_CLIENT_SECRET") +
        "&code=" + code
}

val MOBILE_GITHUB_ACCESS_TOKEN_URI: (code: String) -> String = { code ->
    "/login/oauth/access_token?" +
        "client_id=" + System.getenv("GITHUB_ANDROID_CLIENT_ID") +
        "&client_secret=" + System.getenv("GITHUB_ANDROID_CLIENT_SECRET") +
        "&code=" + code
}

const val GITHUB_USER_ORGS = "/user/orgs"
val GITHUB_ORG_REPOS_URI: (orgName: String, perPage: Int, page: Int) -> String = { org, perPage, page -> "/orgs/$org/repos?type=all" }
val GITHUB_ORG_CREATE_REPO_URI: (orgName: String) -> String = { org -> "/orgs/$org/repos" }
val GITHUB_ORG_TEAMS_URI: (orgName: String) -> String = { org -> "/orgs/$org/teams" }
val GITHUB_ORG_TEAM_URI: (orgName: String, teamName: String) -> String = { org, team -> "/orgs/$org/teams/$team" }
val GITHUB_ORG_TEAMS_USER_URI: (orgName: String, teamName: String, userName: String) -> String = { org, team, user -> "/orgs/$org/teams/$team/memberships/$user" }
val GITHUB_ORG_USER_URI: (orgName: String, userName: String) -> String = { org, user -> "/orgs/$org/memberships/$user" }
