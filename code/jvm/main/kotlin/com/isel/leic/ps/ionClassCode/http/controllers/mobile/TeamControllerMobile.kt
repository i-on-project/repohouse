package com.isel.leic.ps.ionClassCode.http.controllers.mobile

import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.domain.input.UpdateCreateTeamStatusInput
import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveRequestStateInput
import com.isel.leic.ps.ionClassCode.domain.input.request.UpdateRequestStateInput
import com.isel.leic.ps.ionClassCode.http.Uris
import com.isel.leic.ps.ionClassCode.http.model.output.RequestChangeStatusOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.UpdateCreateTeamStatusOutput
import com.isel.leic.ps.ionClassCode.infra.LinkRelation
import com.isel.leic.ps.ionClassCode.infra.siren
import com.isel.leic.ps.ionClassCode.services.TeamServices
import com.isel.leic.ps.ionClassCode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    @GetMapping(Uris.MOBILE_TEAM_REQUESTS_NOT_ACCEPTED_PATH, produces = ["application/vnd.siren+json"])
    fun getTeamRequests(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
        @PathVariable teamId: Int,
    ): ResponseEntity<*> {
        return when (val result = teamServices.getTeamsRequestsForMobile(teamId = teamId)) {
            is Result.Success -> siren(value = result.value) {
                clazz(value = "teamRequests")
                link(rel = LinkRelation(value = "self"), href = Uris.MOBILE_TEAM_REQUESTS_NOT_ACCEPTED_PATH)
            }
            is Result.Problem -> teamServices.problem(error = result.value)
        }
    }

    @PutMapping(Uris.MOBILE_TEAM_REQUESTS_NOT_ACCEPTED_PATH, produces = ["application/vnd.siren+json"])
    fun updateRequestState(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
        @PathVariable teamId: Int,
        @RequestBody body: UpdateRequestStateInput,
    ): ResponseEntity<*> {
        return when (val result = teamServices.updateRequestState(body = body, teamId = teamId)) {
            is Result.Success -> siren(value = RequestChangeStatusOutputModel(id = body.requestId, changed = result.value)) {
                clazz(value = "updateCreateTeamStatus")
                link(rel = LinkRelation(value = "self"), href = Uris.MOBILE_TEAM_CREATE_TEAM_PATH)
            }
            is Result.Problem -> teamServices.problem(error = result.value)
        }
    }

    @DeleteMapping(Uris.MOBILE_TEAM_PATH, produces = ["application/vnd.siren+json"])
    fun deleteTeam(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
        @PathVariable teamId: Int,
        @RequestBody body: LeaveRequestStateInput,
    ): ResponseEntity<*> {
        return when (val result = teamServices.deleteTeam(body = body, teamId = teamId)) {
            is Result.Success -> siren(value = RequestChangeStatusOutputModel(id = body.requestId, changed = result.value)) {
                clazz(value = "deleteTeam")
                link(rel = LinkRelation(value = "self"), href = Uris.MOBILE_TEAM_CREATE_TEAM_PATH)
            }
            is Result.Problem -> teamServices.problem(error = result.value)
        }
    }
}
