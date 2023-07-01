package isel.ps.classcode

val API_HOME = BuildConfig.NGROK_URI

val CLASSCODE_LINK_BUILDER: (String) -> String = { link ->
    "$API_HOME$link"
}

val CLASSCODE_BASE_URL = "$API_HOME/api/mobile"
val CLASSCODE_HOME = "$CLASSCODE_BASE_URL/home"
