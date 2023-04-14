package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.http.Status
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.output.CreditsOutputModel
import com.isel.leic.ps.ion_classcode.infra.JsonHome
import com.isel.leic.ps.ion_classcode.http.model.output.OutputModel
import com.isel.leic.ps.ion_classcode.infra.jsonHome
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.SirenModel
import com.isel.leic.ps.ion_classcode.infra.siren
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
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
    @GetMapping(Uris.HOME, produces = ["application/home+json"])
    fun home(): ResponseEntity<JsonHome> {
        return jsonHome {
            resource(Uris.HOME, listOf(HttpMethod.GET))
            resource(Uris.CREDITS, listOf(HttpMethod.GET))
            resource(Uris.AUTH_STUDENT_PATH, listOf(HttpMethod.GET))
            resource(Uris.AUTH_TEACHER_PATH, listOf(HttpMethod.GET))
            resource(Uris.LOGOUT, listOf(HttpMethod.POST))
            resource(Uris.MENU_PATH, listOf(HttpMethod.GET))
            // TODO(ADD ALL RESOURCES)
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
