package com.isel.leic.ps.ionClassCode.http.controllers.mobile

import com.isel.leic.ps.ionClassCode.http.Uris
import com.isel.leic.ps.ionClassCode.http.model.output.CreditsOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.HomeOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.OutputModel
import com.isel.leic.ps.ionClassCode.infra.LinkRelation
import com.isel.leic.ps.ionClassCode.infra.SirenBuilderScope
import com.isel.leic.ps.ionClassCode.infra.SirenModel
import com.isel.leic.ps.ionClassCode.infra.siren
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SystemControllerMobile {
    @GetMapping(Uris.MOBILE_HOME, produces = ["application/vnd.siren+json"])
    fun home(): ResponseEntity<SirenModel<HomeOutputModel>> {
        return siren(value = HomeOutputModel()) {
            clazz("home")
            allSystemLinks(this)
        }
    }

    @GetMapping(Uris.MOBILE_CREDITS, produces = ["application/vnd.siren+json"])
    fun credits(): ResponseEntity<SirenModel<OutputModel>> {
        return siren(value = CreditsOutputModel()) {
            clazz("credits")
            link(rel = LinkRelation("self"), href = Uris.creditsUri())
        }
    }

    private fun <T> allSystemLinks(block: SirenBuilderScope<T>) {
        block.link(rel = LinkRelation("home"), href = Uris.MOBILE_HOME)
        block.link(rel = LinkRelation("credits"), href = Uris.MOBILE_CREDITS)
        block.link(rel = LinkRelation("auth"), href = Uris.MOBILE_AUTH_PATH)
        block.link(rel = LinkRelation("token"), href = Uris.MOBILE_GET_ACCESS_TOKEN_PATH)
        block.link(rel = LinkRelation("menu"), href = Uris.MOBILE_MENU_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("course"), href = Uris.MOBILE_COURSE_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("classroom"), href = Uris.MOBILE_CLASSROOM_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("assignment"), href = Uris.MOBILE_ASSIGMENT_PATH, needAuthentication = true)
        block.link(rel = LinkRelation("createTeam"), href = Uris.MOBILE_TEAM_CREATE_TEAM_PATH, needAuthentication = true)
    }
}
