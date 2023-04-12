package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.FeedbackInput
import com.isel.leic.ps.ion_classcode.domain.input.request.CreateTeamInput
import com.isel.leic.ps.ion_classcode.domain.input.request.JoinTeamInput
import com.isel.leic.ps.ion_classcode.domain.input.request.LeaveTeamInput
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.output.FeedbackOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.RequestChangeStatusOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.RequestCreatedOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeamOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeamRequestsOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.http.services.TeamServices
import com.isel.leic.ps.ion_classcode.http.services.TeamServicesError
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Team Controller
 * All thw write operations are done by the teacher and need to ensure the classrooms are not archived
 */
@RestController
class TeamController(
    private val teamService: TeamServices,
) {

    /**
     * Get all information about a team
     */
    @GetMapping(Uris.TEAM_PATH)
    fun getTeamInfo(
        user: User,
        @PathVariable teamId: Int,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assigmentId: Int,
    ): ResponseEntity<*> {
        return when (val team = teamService.getTeamInfo(teamId)) {
            is Either.Left -> problem(team.value)
            is Either.Right -> siren(TeamOutputModel(team.value.team, team.value.students, team.value.repos, team.value.feedbacks)) {
                link(href = Uris.teamUri(courseId, classroomId, assigmentId, teamId), rel = LinkRelation("self"), needAuthentication = true)
                link(href = Uris.teamRequestsUri(courseId, classroomId, assigmentId, teamId), rel = LinkRelation("requestsHistory"), needAuthentication = true)
                link(href = Uris.assigmentUri(courseId, classroomId, assigmentId), rel = LinkRelation("assigment"), needAuthentication = true)
                if (user is Student) {
                    action(name = "exitTeam", href = Uris.teamUri(courseId, classroomId, assigmentId, teamId), method = HttpMethod.PUT, type = "application/json", block = {})
                }
                if (user is Teacher) {
                    action(name = "postFeedback", href = Uris.postFeedbackUri(courseId, classroomId, assigmentId, teamId), method = HttpMethod.POST, type = "application/json") {
                        hiddenField(name = "teamId", teamId)
                        textField(name = "description")
                        textField(name = "label")
                    }
                }
            }
        }
    }

    /**
     * Create a request for a student to join a team
     * Needs then to be accepted by the teacher
     */
    @PostMapping(Uris.JOIN_TEAM_PATH)
    fun joinTeam(
        user: User,
        @PathVariable teamId: Int,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assigmentId: Int,
        @RequestBody joinTeamInfo: JoinTeamInput,
    ): ResponseEntity<*> {
        if (user !is Student) return Problem.notStudent
        return when (val join = teamService.joinTeamRequest(joinTeamInfo)) {
            is Either.Left -> problem(join.value)
            is Either.Right -> siren(RequestCreatedOutputModel(join.value, true)) {
                link(href = Uris.teamUri(courseId, classroomId, assigmentId, teamId), rel = LinkRelation("team"), needAuthentication = true)
            }
        }
    }

    /**
     * Create a request for a student to create a team
     * Needs then to be accepted by the teacher
     */
    @PostMapping(Uris.CREATE_TEAM_PATH)
    fun createTeam(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assigmentId: Int,
        @RequestBody createTeamInfo: CreateTeamInput,
    ): ResponseEntity<*> {
        if (user !is Student) return Problem.notStudent
        return when (val create = teamService.createTeamRequest(createTeamInfo, assigmentId, classroomId)) {
            is Either.Left -> problem(create.value)
            is Either.Right -> siren(TeamOutputModel(create.value.team, create.value.students, create.value.repos, create.value.feedbacks)) {
                link(href = Uris.teamUri(courseId, classroomId, assigmentId, create.value.team.id), rel = LinkRelation("team"), needAuthentication = true)
            }
        }
    }

    /**
     * List all the requests history for a team
     */
    @GetMapping(Uris.TEAM_REQUESTS_PATH)
    fun teamRequests(
        user: User,
        @PathVariable teamId: Int,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assigmentId: Int,
    ): ResponseEntity<*> {
        return when (val requests = teamService.getTeamsRequests(teamId)) {
            is Either.Left -> problem(requests.value)
            is Either.Right -> siren(TeamRequestsOutputModel(requests.value.joinTeam, requests.value.leaveTeam)) {
                link(href = Uris.teamUri(courseId, classroomId, assigmentId, teamId), rel = LinkRelation("team"), needAuthentication = true)
                if (user is Teacher) {
                    requests.value.joinTeam.forEach {
                        if (it.state == "Rejected") action(name = "change-status-request", href = Uris.teamChangeStatusRequestsUri(courseId, classroomId, assigmentId, teamId, it.id), method = HttpMethod.POST, type = "application/json", block = {})
                    }
                    requests.value.leaveTeam.forEach {
                        if (it.state == "Rejected") action(name = "change-status-request", href = Uris.teamChangeStatusRequestsUri(courseId, classroomId, assigmentId, teamId, it.id), method = HttpMethod.POST, type = "application/json", block = {})
                    }
                }
            }
        }
    }

    /**
     * Change the status of a request declined to pending again
     */
    @PostMapping(Uris.TEAM_CHANGE_REQUEST_PATH)
    fun changeStatusRequest(
        user: User,
        @PathVariable teamId: Int,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assigmentId: Int,
        @PathVariable requestId: Int,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val change = teamService.updateTeamRequestStatus(requestId, teamId, classroomId)) {
            is Either.Left -> problem(change.value)
            is Either.Right -> siren(RequestChangeStatusOutputModel(requestId, change.value)) {
                link(href = Uris.teamUri(courseId, classroomId, assigmentId, teamId), rel = LinkRelation("team"), needAuthentication = true)
                link(href = Uris.teamRequestsUri(courseId, classroomId, assigmentId, teamId), rel = LinkRelation("requestsHistory"), needAuthentication = true)
            }
        }
    }

    /**
     * Create a request for a student to leave a team
     * Needs then to be accepted by the teacher
     */
    @PostMapping(Uris.EXIT_TEAM_PATH)
    fun exitTeam(
        user: User,
        @PathVariable teamId: Int,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assigmentId: Int,
        @RequestBody leaveTeamInfo: LeaveTeamInput,
    ): ResponseEntity<*> {
        if (user !is Student) return Problem.notStudent
        return when (val exit = teamService.leaveTeamRequest(leaveTeamInfo)) {
            is Either.Left -> problem(exit.value)
            is Either.Right -> siren(RequestCreatedOutputModel(exit.value, true)) {
                link(href = Uris.assigmentUri(courseId, classroomId, assigmentId), rel = LinkRelation("assigment"), needAuthentication = true)
            }
        }
    }

    /**
     * Create a feedback post for a team
     */
    @PostMapping(Uris.POST_FEEDBACK_PATH)
    fun postFeedback(
        user: User,
        @PathVariable teamId: Int,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assigmentId: Int,
        @RequestBody feedbackInfo: FeedbackInput,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val feedback = teamService.postFeedback(feedbackInfo, classroomId)) {
            is Either.Left -> problem(feedback.value)
            is Either.Right -> siren(FeedbackOutputModel(feedback.value, true)) {
                link(href = Uris.teamUri(courseId, classroomId, assigmentId, teamId), rel = LinkRelation("team"), needAuthentication = true)
            }
        }
    }

    /**
     * Function to handle the errors
     */
    private fun problem(error: TeamServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            is TeamServicesError.TeamNotFound -> Problem.notFound
            is TeamServicesError.RequestNotRejected -> Problem.invalidOperation
            is TeamServicesError.RequestNotFound -> Problem.notFound
            is TeamServicesError.ClassroomArchived -> Problem.invalidOperation
            is TeamServicesError.ClassroomNotFound -> Problem.notFound
            is TeamServicesError.AssignmentNotFound -> Problem.notFound
            is TeamServicesError.InvalidData -> Problem.invalidInput
        }
    }
}
