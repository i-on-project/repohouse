package isel.ps.classcode

val API_HOME = "https://e8dc-95-95-191-117.ngrok-free.app"

val CLASSCODE_BASE_URL = "$API_HOME/api/mobile"

val CLASSCODE_AUTH_URL = "$CLASSCODE_BASE_URL/auth"
val CLASSCODE_MENU_URL = "$CLASSCODE_BASE_URL/menu"

val CLASSCODE_TOKEN_URL: (code: String, githubId: String) -> String = { code, githubId ->
    "$CLASSCODE_BASE_URL/token?code=$code&githubId=$githubId"
}
