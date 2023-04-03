package com.isel.leic.ps.ion_classcode.http

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {

    private const val API = "/api"

    private const val WEB = "$API/web"
    private const val MOBILE = "$API/mobile"

    /** Common Uris **/

    const val HOME = "$API/home"
    const val CREDITS = "$API/credits"
    const val AUTH_PATH = "$API/auth"
    const val AUTH_REGISTER_PATH = "$AUTH_PATH/register"
    const val AUTH_REGISTER_VERIFICATION_PATH = "$AUTH_REGISTER_PATH/verify"
    const val AUTH_STUDENT_PATH = "$AUTH_PATH/student"
    const val AUTH_TEACHER_PATH = "$AUTH_PATH/teacher"
    const val AUTH_STATUS_PATH = "$AUTH_PATH/status/{id}"
    const val LOGOUT = "$AUTH_PATH/logout"
    const val CALLBACK_PATH = "$AUTH_PATH/callback"
    const val MENU_PATH = "$API/menu"
    const val TEACHERS_APPROVAL_PATH = "$API/teachers"
    const val STUDENTS_PATH = "$API/students"
    const val STUDENT_PATH = "$STUDENTS_PATH/{id}"
    const val COURSES_PATH = "$API/courses"
    const val COURSE_PATH = "$COURSES_PATH/{courseId}"
    const val STUDENTS_COURSE_PATH = "$COURSE_PATH/students"
    const val ENTER_COURSE_PATH = "$COURSE_PATH/enter"
    const val LEAVE_COURSE_PATH = "$COURSE_PATH/leave"
    const val LEAVE_COURSE_REQUEST_PATH = "$COURSES_PATH/leave/request/{id}"
    const val CLASSROOMS_PATH = "$COURSE_PATH/classrooms"
    const val CREATE_CLASSROOM_PATH = "$CLASSROOMS_PATH/create"
    const val CLASSROOM_PATH = "$CLASSROOMS_PATH/{classroomId}"
    const val ARCHIVE_CLASSROOM_PATH = "$CLASSROOM_PATH/archive"
    const val SYNC_CLASSROOM_PATH = "$CLASSROOM_PATH/sync"
    const val EDIT_CLASSROOM_PATH = "$CLASSROOM_PATH/edit"
    const val INVITE_LINK_PATH = "$API/enter-classroom/{inviteLink}"

    /** Web Uris **/

    /** Functions Uris **/

    fun homeUri(): URI = URI(HOME)
    fun creditsUri(): URI = URI(CREDITS)
    fun authUri(): URI = URI(AUTH_PATH)
    fun authStatusUri(userId: Int): URI = UriTemplate(AUTH_STATUS_PATH).expand(userId)
    fun authUriRegister(): URI = URI(AUTH_REGISTER_PATH)
    fun authUriStudent(): URI = URI(AUTH_STUDENT_PATH)
    fun authUriTeacher(): URI = URI(AUTH_TEACHER_PATH)
    fun authUriRegisterVerification(): URI = URI(AUTH_REGISTER_VERIFICATION_PATH)
    fun callbackUri(): URI = URI(CALLBACK_PATH)
    fun logoutUri(): URI = URI(LOGOUT)

    fun menuUri(): URI = URI(MENU_PATH)
    fun teachersApprovalUri(): URI = URI(TEACHERS_APPROVAL_PATH)
    fun coursesUri(): URI = URI(COURSES_PATH)
    fun courseUri(courseId: Int): URI = UriTemplate(COURSE_PATH).expand(courseId)
    fun courseStudentsUri(courseId: Int): URI = UriTemplate(STUDENTS_COURSE_PATH).expand(courseId)
    fun enterCourse(courseId: Int): URI = UriTemplate(ENTER_COURSE_PATH).expand(courseId)
    fun leaveCourse(courseId: Int): URI = UriTemplate(LEAVE_COURSE_PATH).expand(courseId)
    fun classroomUri(classroomId: Int): URI = UriTemplate(CLASSROOMS_PATH).expand(classroomId)
    fun createClassroomUri(courseId: Int): URI = UriTemplate(CREATE_CLASSROOM_PATH).expand(courseId)
    fun archiveClassroomUri(classroomId: Int): URI = UriTemplate(ARCHIVE_CLASSROOM_PATH).expand(classroomId)
    fun syncClassroomUri(classroomId: Int): URI = UriTemplate(SYNC_CLASSROOM_PATH).expand(classroomId)
    fun editClassroomUri(classroomId: Int): URI = UriTemplate(EDIT_CLASSROOM_PATH).expand(classroomId)
    fun inviteLinkUri(inviteLink: String): URI = UriTemplate(INVITE_LINK_PATH).expand(inviteLink)

    /** Mobile Uris **/

    fun leaveCourseRequestUri(requestId: Int): URI = UriTemplate(LEAVE_COURSE_REQUEST_PATH).expand(requestId)
}
