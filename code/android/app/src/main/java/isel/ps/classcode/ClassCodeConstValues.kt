package isel.ps.classcode

val API_HOME = "https://c846-95-95-191-117.ngrok-free.app"


val CLASSCODE_LINK_BUILDER: (String) -> String = { link ->
    "$API_HOME$link"
}

val CLASSCODE_BASE_URL = "$API_HOME/api/mobile"
val CLASSCODE_HOME = "$CLASSCODE_BASE_URL/home"
