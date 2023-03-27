package com.isel.leic.ps.ion_classcode.http.controllers

import com.isel.leic.ps.ion_classcode.http.Status
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.output.CreditsOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.GithubResponses.*
import com.isel.leic.ps.ion_classcode.http.model.output.HomeOutputModel
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.SirenModel
import com.isel.leic.ps.ion_classcode.infra.siren
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class SystemController {

    @GetMapping(Uris.HOME)
    fun home(): SirenModel<HomeOutputModel> {
        return siren(value = HomeOutputModel()) {
            link(rel = LinkRelation("self"), href = Uris.homeUri())
            link(rel = LinkRelation("credits"), href = Uris.creditsUri())
            link(rel = LinkRelation("authTeacher"), href = Uris.authUriTeacher())
            link(rel = LinkRelation("authStudent"), href = Uris.authUriStudent())
            link(rel = LinkRelation("menu"), href = Uris.menuUri(), needAuthentication = true)
        }
    }

    @GetMapping(Uris.CREDITS)
    fun credits(): SirenModel<CreditsOutputModel> {
        return siren(value = CreditsOutputModel()) {
            link(rel = LinkRelation("self"), href = Uris.creditsUri())
            link(rel = LinkRelation("home"), href = Uris.homeUri())
            link(rel = LinkRelation("menu"), href = Uris.menuUri(), needAuthentication = true)
        }
    }

    @GetMapping("*")
    fun getFallback(): ResponseEntity<Any> {
        return ResponseEntity
            .status(Status.REDIRECT)
            .header(HttpHeaders.LOCATION, Uris.HOME)
            .build()
    }
}