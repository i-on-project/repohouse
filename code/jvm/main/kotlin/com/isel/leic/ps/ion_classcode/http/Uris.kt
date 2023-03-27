package com.isel.leic.ps.ion_classcode.http

import java.net.URI

object Uris {

    private const val API = "/api"

    private const val WEB = "$API/web"
    private const val MOBILE = "$API/mobile"

    /** Common Uris **/

    const val HOME = "$API/home"
    const val CREDITS = "$API/credits"
    const val AUTH_PATH = "$API/auth"
    const val AUTH_STUDENT_PATH = "$AUTH_PATH/student"
    const val AUTH_TEACHER_PATH = "$AUTH_PATH/teacher"
    const val AUTH_STATUS_PATH = "$AUTH_PATH/status"
    const val LOGOUT = "$AUTH_PATH/logout"
    const val CALLBACK_PATH = "$AUTH_PATH/callback"
    const val MENU_PATH = "$API/menu"
    const val TEACHERS_APPROVAL_PATH = "$API/teachers"
    const val COURSES_PATH = "$API/courses"


    /** Web Uris **/

    /** Mobile Uris **/

    /** Functions Uris **/

    fun homeUri(): URI = URI(HOME)
    fun creditsUri(): URI = URI(CREDITS)
    fun authUri(): URI = URI(AUTH_PATH)
    fun authStatusUri(): URI = URI(AUTH_STATUS_PATH)
    fun authUriStudent(): URI = URI(AUTH_STUDENT_PATH)
    fun authUriTeacher(): URI = URI(AUTH_TEACHER_PATH)
    fun callbackUri(): URI = URI(CALLBACK_PATH)
    fun logoutUri(): URI = URI(LOGOUT)
    fun menuUri(): URI = URI(AUTH_PATH)
    fun teachersApprovalUri(): URI = URI(TEACHERS_APPROVAL_PATH)
    fun coursesUri(): URI = URI(COURSES_PATH)
    fun courseUri(courseId: Int): URI = URI("$COURSES_PATH/$courseId")
    fun createCourseUri(): URI = URI("$COURSES_PATH/create")
}
