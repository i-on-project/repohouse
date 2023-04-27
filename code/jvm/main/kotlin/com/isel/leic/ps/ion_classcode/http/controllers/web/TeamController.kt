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
import com.isel.leic.ps.ion_classcode.http.model.output.TeamsOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.services.TeamServices
import com.isel.leic.ps.ion_classcode.services.TeamServicesError
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.utils.Result
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
        @PathVariable assignmentId: Int,
    ): ResponseEntity<*> {
        return when (val team = teamService.getTeamInfo(teamId)) {
            is Result.Problem -> problem(team.value)
            is Result.Success -> siren(TeamOutputModel(team.value.team, team.value.students, team.value.repos, team.value.feedbacks)) {
                clazz("team")
                link(href = Uris.teamUri(courseId, classroomId, assignmentId, teamId), rel = LinkRelation("self"), needAuthentication = true)
            }
        }
    }

    /**
     * Get all information about all teams from an assignment
     */
    @GetMapping(Uris.TEAMS_PATH)
    fun getTeamsInfoByAssignment(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
    ): ResponseEntity<*> {
        return when (val team = teamService.getTeamsInfoByAssignment(assignmentId)) {
            is Result.Problem -> problem(team.value)
            is Result.Success -> siren(TeamsOutputModel(team.value)){
                clazz("teams")
                link(href = Uris.TEAMS_PATH, rel = LinkRelation("self"), needAuthentication = true)
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
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @PathVariable assignmentId: Int,
        @RequestBody joinTeamInfo: JoinTeamInput,
    ): ResponseEntity<*> {
        if (user !is Student) return Problem.notStudent
        return when (val join = teamService.joinTeamRequest(joinTeamInfo,user.id)) {
            is Result.Problem -> problem(join.value)
            is Result.Success -> siren(RequestCreatedOutputModel(join.value, true)) {
                clazz("joinTeam")
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
        @PathVariable assignmentId: Int,
        @RequestBody createTeamInfo: CreateTeamInput,
    ): ResponseEntity<*> {
        if (user !is Student) return Problem.notStudent
        return when (val create = teamService.createTeamRequest(createTeamInfo,user.id, assignmentId, classroomId)) {
            is Result.Problem -> problem(create.value)
            is Result.Success -> siren(RequestCreatedOutputModel(create.value, true)) {
                clazz("createTeam")
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
        @PathVariable assignmentId: Int,
    ): ResponseEntity<*> {
        return when (val requests = teamService.getTeamsRequests(teamId)) {
            is Result.Problem -> problem(requests.value)
            is Result.Success -> siren(TeamRequestsOutputModel(requests.value.joinTeam, requests.value.leaveTeam)) {
                clazz("requests")
                link(href = Uris.teamRequestsUri(courseId, classroomId, assignmentId, teamId), rel = LinkRelation("self"), needAuthentication = true)
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
        @PathVariable assignmentId: Int,
        @PathVariable requestId: Int,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val change = teamService.updateTeamRequestStatus(requestId, teamId, classroomId)) {
            is Result.Problem -> problem(change.value)
            is Result.Success -> siren(RequestChangeStatusOutputModel(requestId, change.value)) {
                link(href = Uris.teamUri(courseId, classroomId, assignmentId, teamId), rel = LinkRelation("team"), needAuthentication = true)
                link(href = Uris.teamRequestsUri(courseId, classroomId, assignmentId, teamId), rel = LinkRelation("requestsHistory"), needAuthentication = true)
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
        @PathVariable assignmentId: Int,
        @RequestBody leaveTeamInfo: LeaveTeamInput,
    ): ResponseEntity<*> {
        if (user !is Student) return Problem.notStudent
        return when (val exit = teamService.leaveTeamRequest(leaveTeamInfo,user.id)) {
            is Result.Problem -> problem(exit.value)
            is Result.Success -> siren(RequestCreatedOutputModel(exit.value, true)) {
                clazz("leaveTeam")
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
        @PathVariable assignmentId: Int,
        @RequestBody feedbackInfo: FeedbackInput,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val feedback = teamService.postFeedback(feedbackInfo, classroomId)) {
            is Result.Problem -> problem(feedback.value)
            is Result.Success -> siren(FeedbackOutputModel(feedback.value, true)) {
                link(href = Uris.teamUri(courseId, classroomId, assignmentId, teamId), rel = LinkRelation("team"), needAuthentication = true)
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
