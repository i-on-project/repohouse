package com.isel.leic.ps.ionClassCode.http

import com.isel.leic.ps.ionClassCode.http.controllers.web.TEST
import com.isel.leic.ps.ionClassCode.http.controllers.web.URI

const val GITHUB_BASE_URL = "https://github.com"
const val GITHUB_API_BASE_URL = "https://api.github.com"

val CLIENT_ID: String = if (TEST) System.getenv("GITHUB_CLIENT_ID_TESTS") else System.getenv("GITHUB_CLIENT_ID")
val CLIENT_SECRET: String = if (TEST) System.getenv("GITHUB_CLIENT_SECRET_TESTS") else System.getenv("GITHUB_CLIENT_SECRET")

val GITHUB_OAUTH_URI: (scope: String, state: String) -> String = { scope, state ->
    "/login/oauth/authorize?" +
        "client_id=$CLIENT_ID" +
        "&scope=$scope" +
        "&state=$state" +
        "&redirect_uri=$URI/api/auth/callback"
}

val MOBILE_GITHUB_OAUTH_URI: (scope: String, state: String) -> String = { scope, state ->
    "/login/oauth/authorize?" +
        "client_id=$CLIENT_ID" +
        "&scope=$scope" +
        "&state=$state" +
        "&redirect_uri=$URI/api/auth/callback/mobile"
}

const val GITHUB_USERINFO_URI = "/user"
const val GITHUB_USERMAILS_URI = "$GITHUB_USERINFO_URI/emails"

val GITHUB_ACCESS_TOKEN_URI: (code: String) -> String = { code ->
    "/login/oauth/access_token?" +
        "client_id=$CLIENT_ID" +
        "&client_secret=$CLIENT_SECRET" +
        "&code=" + code
}

const val GITHUB_USER_ORGS = "/user/orgs"
