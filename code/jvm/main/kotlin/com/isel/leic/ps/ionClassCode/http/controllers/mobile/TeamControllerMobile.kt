package com.isel.leic.ps.ionClassCode.http.controllers.mobile

import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.domain.input.UpdateCreateTeamStatusInput
import com.isel.leic.ps.ionClassCode.http.Uris
import com.isel.leic.ps.ionClassCode.http.model.output.UpdateCreateTeamStatusOutput
import com.isel.leic.ps.ionClassCode.infra.LinkRelation
import com.isel.leic.ps.ionClassCode.infra.siren
import com.isel.leic.ps.ionClassCode.services.TeamServices
import com.isel.leic.ps.ionClassCode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TeamControllerMobile(
    private val teamServices: TeamServices,
) {
    @PostMapping(Uris.MOBILE_TEAM_CREATE_TEAM_PATH, produces = ["application/vnd.siren+json"])
    fun updateTeamState(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
        @PathVariable teamId: Int,
        @RequestBody body: UpdateCreateTeamStatusInput,
    ): ResponseEntity<*> {
        return when (val result = teamServices.updateCreateTeamCompositeRequest(body = body, teamId = teamId)) {
            is Result.Success -> siren(value = UpdateCreateTeamStatusOutput(result = result.value)) {
                clazz(value = "updateCreateTeamStatus")
                link(rel = LinkRelation(value = "self"), href = Uris.MOBILE_TEAM_CREATE_TEAM_PATH)
            }
            is Result.Problem -> teamServices.problem(error = result.value)
        }
    }
}
