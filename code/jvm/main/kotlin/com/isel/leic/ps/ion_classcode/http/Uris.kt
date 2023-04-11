package com.isel.leic.ps.ion_classcode.http

import org.springframework.web.util.UriTemplate
import java.net.URI

/**
 * Uris used in the application.
 */
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
    const val CLASSROOMS_PATH = "$COURSE_PATH/classrooms"
    const val CLASSROOM_PATH = "$CLASSROOMS_PATH/{classroomId}"
    const val LEAVE_COURSE_REQUEST_PATH = "$COURSES_PATH/leave/request/{id}"
    const val CREATE_CLASSROOM_PATH = "$CLASSROOMS_PATH/create"
    const val ARCHIVE_CLASSROOM_PATH = "$CLASSROOM_PATH/archive"
    const val SYNC_CLASSROOM_PATH = "$CLASSROOM_PATH/sync"
    const val EDIT_CLASSROOM_PATH = "$CLASSROOM_PATH/edit"
    const val INVITE_LINK_PATH = "$COURSE_PATH/enter-classroom/{inviteLink}"
    const val ASSIGMENTS_PATH = "$CLASSROOM_PATH/assigments"
    const val ASSIGMENT_PATH = "$ASSIGMENTS_PATH/{assigmentId}"
    const val CREATE_ASSIGMENT_PATH = "$ASSIGMENTS_PATH/create"
    const val DELIVERYS_PATH = "$ASSIGMENT_PATH/deliveries"
    const val DELIVERY_PATH = "$DELIVERYS_PATH/{deliveryId}"
    const val CREATE_DELIVERY_PATH = "$DELIVERYS_PATH/create"
    const val DELETE_ASSIGMENT_PATH = "$ASSIGMENT_PATH/delete"
    const val TEAMS_PATH = "$ASSIGMENT_PATH/teams"
    const val TEAM_PATH = "$TEAMS_PATH/{teamId}"
    const val CREATE_TEAM_PATH = "$TEAMS_PATH/create"
    const val JOIN_TEAM_PATH = "$TEAM_PATH/join"
    const val EXIT_TEAM_PATH = "$TEAM_PATH/exit"
    const val EDIT_DELIVERY_PATH = "$DELIVERY_PATH/edit"
    const val SYNC_DELIVERY_PATH = "$DELIVERY_PATH/sync"
    const val LOCAL_COPY_PATH = "$CLASSROOM_PATH/copy"
    const val TEAM_REQUESTS_PATH = "$TEAM_PATH/requests"
    const val TEAM_CHANGE_REQUEST_PATH = "$TEAM_REQUESTS_PATH/{requestId}"
    const val POST_FEEDBACK_PATH = "$TEAM_PATH/feedback"

    /** Web Uris **/

    /** Functions Uris **/

    fun homeUri(): URI = URI(HOME)
    fun creditsUri(): URI = URI(CREDITS)
    fun authUri(): URI = URI(AUTH_PATH)
    fun authUriRegister(): URI = URI(AUTH_REGISTER_PATH)
    fun authUriStudent(): URI = URI(AUTH_STUDENT_PATH)
    fun authUriTeacher(): URI = URI(AUTH_TEACHER_PATH)
    fun authUriRegisterVerification(): URI = URI(AUTH_REGISTER_VERIFICATION_PATH)
    fun callbackUri(): URI = URI(CALLBACK_PATH)
    fun logoutUri(): URI = URI(LOGOUT)

    fun menuUri(): URI = URI(MENU_PATH)
    fun teachersApprovalUri(): URI = URI(TEACHERS_APPROVAL_PATH)
    fun studentsUri(userId: Int): URI = UriTemplate(ENTER_COURSE_PATH).expand(userId)

    fun coursesUri(): URI = URI(COURSES_PATH)
    fun courseUri(courseId: Int): URI = UriTemplate(COURSE_PATH).expand(courseId)
    fun courseStudentsUri(courseId: Int): URI = UriTemplate(STUDENTS_COURSE_PATH).expand(courseId)
    fun enterCourse(courseId: Int): URI = UriTemplate(ENTER_COURSE_PATH).expand(courseId)
    fun leaveCourse(courseId: Int): URI = UriTemplate(LEAVE_COURSE_PATH).expand(courseId)
    fun classroomUri(courseId: Int, classroomId: Int): URI = UriTemplate(CLASSROOMS_PATH).expand(courseId, classroomId)
    fun createClassroomUri(courseId: Int): URI = UriTemplate(CREATE_CLASSROOM_PATH).expand(courseId)
    fun archiveClassroomUri(courseId: Int, classroomId: Int): URI = UriTemplate(ARCHIVE_CLASSROOM_PATH).expand(courseId, classroomId)
    fun syncClassroomUri(courseId: Int, classroomId: Int): URI = UriTemplate(SYNC_CLASSROOM_PATH).expand(courseId, classroomId)
    fun editClassroomUri(courseId: Int, classroomId: Int): URI = UriTemplate(EDIT_CLASSROOM_PATH).expand(courseId, classroomId)
    fun inviteLinkUri(courseId: Int, inviteLink: String): URI = UriTemplate(INVITE_LINK_PATH).expand(courseId, inviteLink)
    fun assigmentsUri(courseId: Int, classroomId: Int): URI = UriTemplate(ASSIGMENTS_PATH).expand(courseId, classroomId)
    fun assigmentUri(courseId: Int, classroomId: Int, assigmentId: Int): URI = UriTemplate(ASSIGMENT_PATH).expand(courseId, classroomId, assigmentId)
    fun createAssigmentUri(courseId: Int, classroomId: Int): URI = UriTemplate(CREATE_ASSIGMENT_PATH).expand(courseId, classroomId)
    fun deliverysUri(courseId: Int, classroomId: Int, assigmentId: Int): URI = UriTemplate(DELIVERYS_PATH).expand(courseId, classroomId, assigmentId)
    fun deliveryUri(courseId: Int, classroomId: Int, assigmentId: Int, deliveryId: Int): URI = UriTemplate(DELIVERY_PATH).expand(courseId, classroomId, assigmentId, deliveryId)
    fun createDeliveryUri(courseId: Int, classroomId: Int, assigmentId: Int): URI = UriTemplate(CREATE_DELIVERY_PATH).expand(courseId, classroomId, assigmentId)
    fun deleteAssigmentUri(courseId: Int, classroomId: Int, assigmentId: Int): URI = UriTemplate(DELETE_ASSIGMENT_PATH).expand(courseId, classroomId, assigmentId)
    fun teamsUri(courseId: Int, classroomId: Int, assigmentId: Int): URI = UriTemplate(TEAMS_PATH).expand(courseId, classroomId, assigmentId)
    fun teamUri(courseId: Int, classroomId: Int, assigmentId: Int, teamId: Int): URI = UriTemplate(TEAM_PATH).expand(courseId, classroomId, assigmentId, teamId)
    fun editDeliveryUri(courseId: Int, classroomId: Int, assigmentId: Int, deliveryId: Int): URI = UriTemplate(EDIT_DELIVERY_PATH).expand(courseId, classroomId, assigmentId, deliveryId)
    fun syncDeliveryUri(courseId: Int, classroomId: Int, assigmentId: Int, deliveryId: Int): URI = UriTemplate(SYNC_DELIVERY_PATH).expand(courseId, classroomId, assigmentId, deliveryId)
    fun createTeamUri(courseId: Int, classroomId: Int, assigmentId: Int): URI = UriTemplate(CREATE_TEAM_PATH).expand(courseId, classroomId, assigmentId)
    fun joinTeamUri(courseId: Int, classroomId: Int, assigmentId: Int, teamId: Int): URI = UriTemplate(JOIN_TEAM_PATH).expand(courseId, classroomId, assigmentId, teamId)
    fun teamRequestsUri(courseId: Int, classroomId: Int, assigmentId: Int, teamId: Int): URI = UriTemplate(TEAM_REQUESTS_PATH).expand(courseId, classroomId, assigmentId, teamId)
    fun teamChangeStatusRequestsUri(courseId: Int, classroomId: Int, assigmentId: Int, teamId: Int, requestId: Int): URI = UriTemplate(
        TEAM_CHANGE_REQUEST_PATH
    ).expand(courseId, classroomId, assigmentId, teamId, requestId)
    fun postFeedbackUri(courseId: Int, classroomId: Int, assigmentId: Int, teamId: Int): URI = UriTemplate(POST_FEEDBACK_PATH).expand(courseId, classroomId, assigmentId, teamId)
    fun localCopyUri(courseId: Int, classroomId: Int): URI = UriTemplate(LOCAL_COPY_PATH).expand(courseId, classroomId)

    /** Mobile Uris **/
}
