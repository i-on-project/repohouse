package isel.ps.classcode

val API_HOME = "https://577d-95-95-191-117.ngrok-free.app"

val CLASSCODE_BASE_URL = "$API_HOME/api/mobile"

val CLASSCODE_AUTH_URL = "$CLASSCODE_BASE_URL/auth"
val CLASSCODE_MENU_URL = "$CLASSCODE_BASE_URL/menu"
val CLASSCODE_COURSE_URL: (Int) -> String = { courseId ->
    "$CLASSCODE_BASE_URL/courses/$courseId"
}
val CLASSCODE_CLASSROOM_URL: (Int, Int) -> String = { courseId, classroomId ->
    CLASSCODE_COURSE_URL(courseId) + "/classrooms/$classroomId"
}

val CLASSCODE_ASSIGNMENT_URL: (Int, Int, Int) -> String = { courseId, classroomId, assignmentId ->
    CLASSCODE_CLASSROOM_URL(courseId, classroomId) + "/assignments/$assignmentId"
}

val CLASSCODE_TOKEN_URL: (code: String, githubId: String) -> String = { code, githubId ->
    "$CLASSCODE_BASE_URL/token?code=$code&githubId=$githubId"
}
