package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.output.CreditsOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.HomeOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.OutputModel
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.SirenBuilderScope
import com.isel.leic.ps.ion_classcode.infra.SirenModel
import com.isel.leic.ps.ion_classcode.infra.siren
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * System Controller
 */
@RestController
class SystemController {

    /**
     * Home page
     */
    @GetMapping(Uris.HOME, produces = ["application/vnd.siren+json"])
    fun home(): ResponseEntity<SirenModel<HomeOutputModel>> {
        return siren(value = HomeOutputModel()) {
            clazz("home")
            allSystemLinks(this)
            allSystemActions(this)
        }
    }

    /**
     * Credits page
     */
    @GetMapping(Uris.CREDITS, produces = ["application/vnd.siren+json"])
    fun credits(): ResponseEntity<SirenModel<OutputModel>> {
        return siren(value = CreditsOutputModel()) {
            clazz("credits")
            link(rel = LinkRelation("self"), href = Uris.creditsUri())
        }
    }

    private fun <T> allSystemLinks(block:SirenBuilderScope<T>) {
        block.link(rel = LinkRelation("home"), href = Uris.HOME)
        block.link(rel = LinkRelation("credits"), href = Uris.CREDITS)
        block.link(rel = LinkRelation("menu"), href = Uris.MENU_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("authTeacher"), href = Uris.AUTH_TEACHER_PATH)
        block.link(rel = LinkRelation("authStudent"), href = Uris.AUTH_STUDENT_PATH)
        block.link(rel = LinkRelation("registerInfo"), href = Uris.AUTH_REGISTER_PATH)
        block.link(rel = LinkRelation("status"), href = Uris.AUTH_STATUS_PATH)
        block.link(rel = LinkRelation("state"), href = Uris.AUTH_STATE_PATH)
        block.link(rel = LinkRelation("orgs"), href = Uris.ORGS_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("course"), href = Uris.COURSE_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("teachersApproval"), href = Uris.TEACHERS_APPROVAL_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("classroom"), href = Uris.CLASSROOM_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("local"), href = Uris.LOCAL_COPY_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("assignment"), href = Uris.ASSIGMENT_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("assigments"), href = Uris.ASSIGMENTS_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("delivery"), href = Uris.DELIVERY_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("deliveries"), href = Uris.DELIVERIES_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("team"), href = Uris.TEAM_PATH,needAuthentication = true)
        block.link(rel = LinkRelation("teams"), href = Uris.TEAMS_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("requestsHistory"),href = Uris.TEAM_REQUESTS_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("localCopy"), href = Uris.LOCAL_COPY_PATH, needAuthentication = true)
    }

    private fun <T> allSystemActions(block:SirenBuilderScope<T>) {
        block.action(title = "approveTeacher", href = Uris.TEACHERS_APPROVAL_PATH, method = HttpMethod.POST, type = "application/x-www-form-urlencoded", block = {
            rangeField(name = "approved")
            rangeField(name = "rejected")
        })
        block.action(title = "registerStudent", href = Uris.AUTH_REGISTER_STUDENT_PATH, method = HttpMethod.POST, type = "application/json", block = {
            numberField("schoolId")
        })
        block.action(title = "registerTeacher", href = Uris.AUTH_REGISTER_TEACHER_PATH, method = HttpMethod.POST, type = "application/json", block = {})
        block.action(title = "verify", href = Uris.AUTH_REGISTER_VERIFICATION_PATH, method = HttpMethod.POST, type = "application/json", block = {
            numberField("otp")
        })
        block.action(title ="resendEmail", href = Uris.AUTH_RESEND_EMAIL_PATH, method = HttpMethod.POST, type = "application/json", block = {})
        block.action(title = "logout", href = Uris.LOGOUT, method = HttpMethod.POST, type = "application/json", block = {})
        block.action(title = "createCourse", href = Uris.COURSES_PATH, method = HttpMethod.POST, type = "application/json", block = {
            textField("orgUrl")
            textField("name")
        })
        block.action(title = "leaveCourse", href = Uris.LEAVE_COURSE_PATH, method = HttpMethod.PUT, type = "application/json", block = {})
        block.action(title = "archiveCourse", href = Uris.COURSE_PATH, method = HttpMethod.PUT, type = "application/json", block = {})
        block.action(title = "createClassroom", href = Uris.CREATE_CLASSROOM_PATH, method = HttpMethod.POST, type = "application/json", block = {
            textField("name")
        })
        block.action(title = "archiveClassroom", href = Uris.ARCHIVE_CLASSROOM_PATH, method = HttpMethod.PUT, type = "application/json", block = {})
        block.action(title = "editClassroom", href = Uris.EDIT_CLASSROOM_PATH, method = HttpMethod.PUT, type = "application/json", block = {
            textField("name")
        })
        block.action(title = "classroomInvite", href = Uris.INVITE_LINK_PATH, method = HttpMethod.POST, type = "application/json", block = {
            textField("name")
        })
        block.action(title = "syncClassroom", href = Uris.SYNC_CLASSROOM_PATH, method = HttpMethod.PUT, type = "application/json", block = {})
        block.action("joinTeam", Uris.JOIN_TEAM_PATH, method = HttpMethod.POST, type = "application/json",) {
            hiddenField("assigmentId")
            numberField("teamId")
            numberField("composite",null)
        }
        block.action("createTeam", Uris.CREATE_TEAM_PATH, method = HttpMethod.POST, type = "application/json") {
            hiddenField("assigmentId")
            numberField("composite",null)
        }
        block.action("leaveTeam", Uris.EXIT_TEAM_PATH, method = HttpMethod.POST, type = "application/json") {
            hiddenField("teamId")
            numberField("composite",null)
        }
        block.action(title = "deleteDelivery", href = Uris.DELIVERY_PATH, method = HttpMethod.DELETE, type = "application/json",) {}
        block.action(title = "syncDelivery", href = Uris.SYNC_DELIVERY_PATH, method = HttpMethod.POST, type = "application/json",) {}
        block.action(title = "postFeedback", href = Uris.POST_FEEDBACK_PATH, method = HttpMethod.POST, type = "application/json") {
            hiddenField(name = "teamId")
            textField(name = "description")
            textField(name = "label")
        }
        block.action(title = "changeStatusRequest", href = Uris.TEAM_CHANGE_REQUEST_PATH, method = HttpMethod.POST, type = "application/json") {}
        block.action(title = "inviteCode", href = Uris.INVITE_LINK_PATH, method = HttpMethod.POST, type = "application/json") {}
        block.action(title= "archiveClassroom", href = Uris.ARCHIVE_CLASSROOM_PATH, method = HttpMethod.PUT, type = "application/json") {}
        block.action(title = "createClassroom", href = Uris.CREATE_CLASSROOM_PATH, method = HttpMethod.POST, type = "application/json") {
            textField(name = "name")
        }
        block.action(title = "createAssignment", href = Uris.CREATE_ASSIGNMENT_PATH, method = HttpMethod.POST, type = "application/json") {
            textField(name = "name")
        }
        block.action(title = "deleteAssignment", href = Uris.ASSIGMENT_PATH, method = HttpMethod.DELETE, type = "application/json") {}
    }
}
