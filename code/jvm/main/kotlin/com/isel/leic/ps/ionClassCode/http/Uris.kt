package com.isel.leic.ps.ionClassCode.http

import org.springframework.web.util.UriTemplate
import java.net.URI

/**
 * Uris used in the application.
 */
object Uris {

    const val API = "/api"

    /** Common Uris **/

    const val HOME = "$API/home"
    const val CREDITS = "$API/credits"
    private const val AUTH_PATH = "$API/auth"
    const val AUTH_STUDENT_PATH = "$AUTH_PATH/student"
    const val AUTH_TEACHER_PATH = "$AUTH_PATH/teacher"
    const val CALLBACK_PATH = "$AUTH_PATH/callback"
    const val AUTH_REGISTER_PATH = "$AUTH_PATH/register"
    const val AUTH_REGISTER_STUDENT_PATH = "$AUTH_REGISTER_PATH/student"
    const val AUTH_REGISTER_TEACHER_PATH = "$AUTH_REGISTER_PATH/teacher"
    const val AUTH_STATUS_PATH = "$AUTH_PATH/status"
    const val AUTH_STATE_PATH = "$AUTH_PATH/state"
    const val AUTH_RESEND_EMAIL_PATH = "$AUTH_REGISTER_STUDENT_PATH/resend"
    const val AUTH_REGISTER_VERIFICATION_PATH = "$AUTH_REGISTER_STUDENT_PATH/verify"
    const val LOGOUT = "$AUTH_PATH/logout"
    const val MENU_PATH = "$API/menu"
    const val ORGS_PATH = "$API/orgs"
    const val COURSES_PATH = "$API/courses"
    const val COURSE_PATH = "$COURSES_PATH/{courseId}"

    const val TEACHERS_APPROVAL_PATH = "$API/teachers"
    const val STUDENTS_PATH = "$API/students"
    const val STUDENTS_COURSE_PATH = "$COURSE_PATH/students"
    const val ENTER_COURSE_PATH = "$COURSE_PATH/enter"
    const val LEAVE_COURSE_PATH = "$COURSE_PATH/leave"
    const val CLASSROOMS_PATH = "$COURSE_PATH/classrooms"
    const val CLASSROOM_PATH = "$CLASSROOMS_PATH/{classroomId}"
    const val LEAVE_CLASSROOM_PATH = "$CLASSROOMS_PATH/leave"
    const val CREATE_CLASSROOM_PATH = "$CLASSROOMS_PATH/create"
    const val ARCHIVE_CLASSROOM_PATH = "$CLASSROOM_PATH/archive"
    const val SYNC_CLASSROOM_PATH = "$CLASSROOM_PATH/sync"
    const val EDIT_CLASSROOM_PATH = "$CLASSROOM_PATH/edit"
    const val INVITE_LINK_PATH = "$API/invite/{inviteLink}"
    const val ASSIGMENTS_PATH = "$CLASSROOM_PATH/assignments"
    const val ASSIGMENT_PATH = "$ASSIGMENTS_PATH/{assignmentId}"
    const val CREATE_ASSIGNMENT_PATH = "$ASSIGMENTS_PATH/create"
    const val DELIVERIES_PATH = "$ASSIGMENT_PATH/deliveries"
    const val DELIVERY_PATH = "$DELIVERIES_PATH/{deliveryId}"
    const val CREATE_DELIVERY_PATH = "$DELIVERIES_PATH/create"
    const val DELETE_ASSIGMENT_PATH = "$ASSIGMENT_PATH/delete"
    const val TEAMS_PATH = "$ASSIGMENT_PATH/teams"
    const val TEAM_PATH = "$TEAMS_PATH/{teamId}"
    const val CREATE_TEAM_PATH = "$TEAMS_PATH/create"
    const val JOIN_TEAM_PATH = "$TEAMS_PATH/join"
    const val CLOSE_TEAM_PATH = "$TEAM_PATH/close"
    const val EXIT_TEAM_PATH = "$TEAM_PATH/exit"
    const val EDIT_DELIVERY_PATH = "$DELIVERY_PATH/edit"
    const val SYNC_DELIVERY_PATH = "$DELIVERY_PATH/sync"
    const val LOCAL_COPY_PATH = "$CLASSROOM_PATH/copy"
    const val TEAM_REQUESTS_PATH = "$TEAM_PATH/requests"
    const val TEAM_CHANGE_REQUEST_PATH = "$TEAM_REQUESTS_PATH/{requestId}"
    const val POST_FEEDBACK_PATH = "$TEAM_PATH/feedback"

    /** Functions Uris **/
    fun creditsUri(): String = URI(CREDITS).toASCIIString()
    fun courseUri(courseId: Int): String = UriTemplate(COURSE_PATH).expand(courseId).toASCIIString()
    fun leaveCourse(courseId: Int): String = UriTemplate(LEAVE_COURSE_PATH).expand(courseId).toASCIIString()
    fun classroomUri(courseId: Int, classroomId: Int): String = UriTemplate(CLASSROOM_PATH).expand(courseId, classroomId).toASCIIString()
    fun leaveClassroomUri(courseId: Int, classroomId: Int): String = UriTemplate(LEAVE_CLASSROOM_PATH).expand(courseId, classroomId).toASCIIString()
    fun syncClassroomUri(courseId: Int, classroomId: Int): String = UriTemplate(SYNC_CLASSROOM_PATH).expand(courseId, classroomId).toASCIIString()
    fun editClassroomUri(courseId: Int, classroomId: Int): String = UriTemplate(EDIT_CLASSROOM_PATH).expand(courseId, classroomId).toASCIIString()
    fun assigmentUri(courseId: Int, classroomId: Int, assignmentId: Int): String = UriTemplate(ASSIGMENT_PATH).expand(courseId, classroomId, assignmentId).toASCIIString()
    fun deliveriesUri(courseId: Int, classroomId: Int, assigmentId: Int): String = UriTemplate(DELIVERIES_PATH).expand(courseId, classroomId, assigmentId).toASCIIString()
    fun deliveryUri(courseId: Int, classroomId: Int, assigmentId: Int, deliveryId: Int): String = UriTemplate(DELIVERY_PATH).expand(courseId, classroomId, assigmentId, deliveryId).toASCIIString()
    fun teamUri(courseId: Int, classroomId: Int, assignmentId: Int, teamId: Int): String = UriTemplate(TEAM_PATH).expand(courseId, classroomId, assignmentId, teamId).toASCIIString()
    fun teamRequestsUri(courseId: Int, classroomId: Int, assigmentId: Int, teamId: Int): String = UriTemplate(TEAM_REQUESTS_PATH).expand(courseId, classroomId, assigmentId, teamId).toASCIIString()

    /** Mobile Uris **/

    const val MOBILE_API = "/api/mobile"
    const val MOBILE_HOME = "$MOBILE_API/home"
    const val MOBILE_CREDITS = "$MOBILE_API/credits"
    const val MOBILE_AUTH_PATH = "$MOBILE_API/auth"
    const val MOBILE_GET_ACCESS_TOKEN_PATH = "$MOBILE_API/token"
    const val MOBILE_MENU_PATH = "$MOBILE_API/menu"
    const val MOBILE_COURSES_PATH = "$MOBILE_API/courses"
    const val MOBILE_COURSE_PATH = "$MOBILE_COURSES_PATH/{courseId}"
    const val MOBILE_LEAVE_COURSE_PATH = "$MOBILE_COURSE_PATH/users/{userId}"
    const val MOBILE_CLASSROOMS_PATH = "$MOBILE_COURSE_PATH/classrooms"
    const val MOBILE_CLASSROOM_PATH = "$MOBILE_CLASSROOMS_PATH/{classroomId}"
    const val MOBILE_LEAVE_CLASSROOM_PATH = "$MOBILE_CLASSROOM_PATH/users/{userId}"
    const val MOBILE_CLASSROOM_ARCHIVED_PATH = "$MOBILE_CLASSROOM_PATH/archived"
    const val MOBILE_ASSIGMENTS_PATH = "$MOBILE_CLASSROOM_PATH/assignments"
    const val MOBILE_ASSIGMENT_PATH = "$MOBILE_ASSIGMENTS_PATH/{assignmentId}"
    const val MOBILE_TEAMS_PATH = "$MOBILE_ASSIGMENT_PATH/teams"
    const val MOBILE_TEAM_PATH = "$MOBILE_TEAMS_PATH/{teamId}"
    const val MOBILE_TEAM_CREATE_TEAM_PATH = "$MOBILE_TEAM_PATH/create"
    const val MOBILE_TEAM_REQUESTS_NOT_ACCEPTED_PATH = "$MOBILE_TEAM_PATH/requests"
}
