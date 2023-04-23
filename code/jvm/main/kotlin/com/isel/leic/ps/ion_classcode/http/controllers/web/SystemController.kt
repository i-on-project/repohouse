package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.output.CreditsOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.HomeOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.OutputModel
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
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
            link(rel = LinkRelation("home"), href = Uris.HOME)
            link(rel = LinkRelation("credits"), href = Uris.CREDITS)
            link(rel = LinkRelation("menu"), href = Uris.MENU_PATH, needAuthentication = true)
            link(rel = LinkRelation("authTeacher"), href = Uris.AUTH_TEACHER_PATH)
            link(rel = LinkRelation("authStudent"), href = Uris.AUTH_STUDENT_PATH)
            link(rel = LinkRelation("registerInfo"), href = Uris.AUTH_REGISTER_PATH)
            action(title = "registerStudent", href = Uris.AUTH_REGISTER_STUDENT_PATH, method = HttpMethod.POST, type = "application/json", block = {
                numberField("schoolId")
            })
            action(title = "registerTeacher", href = Uris.AUTH_REGISTER_TEACHER_PATH, method = HttpMethod.POST, type = "application/json", block = {})
            link(rel = LinkRelation("status"), href = Uris.AUTH_STATUS_PATH)
            action(title = "verify", href = Uris.AUTH_REGISTER_VERIFICATION_PATH, method = HttpMethod.POST, type = "application/json", block = {
                numberField("otp")
            })
            action(title = "logout", href = Uris.LOGOUT, method = HttpMethod.POST, type = "application/json", block = {})
            link(rel = LinkRelation("orgs"), href = Uris.ORGS_PATH)
            action(title = "createCourse", href = Uris.COURSES_PATH, method = HttpMethod.POST, type = "application/json", block = {
                textField("orgUrl")
                textField("name")
                numberField("teacherId")
            })
            link(rel = LinkRelation("course"), href = Uris.COURSE_PATH)
            action(title = "teachersApproval", href = Uris.TEACHERS_APPROVAL_PATH, method = HttpMethod.POST, type = "application/json", block = {
                rangeField("approved")
                rangeField("rejected")
            })
            link(rel = LinkRelation("teachersApproval"), href = Uris.TEACHERS_APPROVAL_PATH)
        }
    }

    /**
     * Credits page
     */
    @GetMapping(Uris.CREDITS, produces = ["application/vnd.siren+json"])
    fun credits(): ResponseEntity<SirenModel<OutputModel>> {
        return siren(value = CreditsOutputModel()) {
            link(rel = LinkRelation("self"), href = Uris.creditsUri())
            link(rel = LinkRelation("home"), href = Uris.homeUri())
            link(rel = LinkRelation("menu"), href = Uris.menuUri(), needAuthentication = true)
        }
    }

}
