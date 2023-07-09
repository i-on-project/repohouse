package isel.ps.classcode.presentation.team.services

import com.damnhandy.uri.template.UriTemplate
import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.CLASSCODE_LINK_BUILDER
import isel.ps.classcode.MEDIA_TYPE
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.domain.LeaveRequestStateInput
import isel.ps.classcode.domain.TeamRequests
import isel.ps.classcode.domain.UpdateRequestStateInput
import isel.ps.classcode.domain.deserialization.ClassCodeTeamRequestDto
import isel.ps.classcode.domain.deserialization.ClassCodeTeamRequestsType
import isel.ps.classcode.http.NavigationRepository
import isel.ps.classcode.http.handleSirenResponseClassCode
import isel.ps.classcode.http.send
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.REQUESTS_NOT_ACCEPTED_KEY
import isel.ps.classcode.presentation.TEAM_KEY
import isel.ps.classcode.presentation.bootUp.services.BootUpServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Implementation of the [TeamServices] interface that will be used for the real app
 */
class RealTeamServices(private val httpClient: OkHttpClient, private val objectMapper: ObjectMapper, private val sessionStore: SessionStore, private val navigationRepo: NavigationRepository, private val bootUpServices: BootUpServices) : TeamServices {

    override suspend fun getTeamRequests(courseId: Int, classroomId: Int, assignmentId: Int, teamId: Int): Either<HandleClassCodeResponseError, TeamRequests> {
        val ensureLink = navigationRepo.ensureLink(key = REQUESTS_NOT_ACCEPTED_KEY, fetchLink = { bootUpServices.getHome() }) ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
        val uri = UriTemplate.fromTemplate(ensureLink.href)
            .set("courseId", courseId)
            .set("classroomId", classroomId)
            .set("assignmentId", assignmentId)
            .set("teamId", teamId)
            .expand()
        val cookie = sessionStore.getSessionCookie()
        val request = Request.Builder()
            .url(CLASSCODE_LINK_BUILDER(uri))
            .addHeader("Cookie", cookie.first())
            .build()
        val result = request.send(httpClient) { response ->
            handleSirenResponseClassCode<ClassCodeTeamRequestDto>(response = response, type = ClassCodeTeamRequestsType, jsonMapper = objectMapper)
        }
        return when (result) {
            is Either.Right -> {
                val teamRequests = TeamRequests(deserialization = result.value.properties)
                Either.Right(value = teamRequests)
            }
            is Either.Left -> Either.Left(value = result.value)
        }
    }

    override suspend fun updateStateOfRequestInClassCode(courseId: Int, classroomId: Int, assignmentId: Int, teamId: Int, creator: Int, requestId: Int, state: String, isJoinTeam: Boolean): Either<HandleClassCodeResponseError, Unit> {
        val ensureLink = navigationRepo.ensureLink(key = REQUESTS_NOT_ACCEPTED_KEY, fetchLink = { bootUpServices.getHome() }) ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
        val uri = UriTemplate.fromTemplate(ensureLink.href)
            .set("courseId", courseId)
            .set("classroomId", classroomId)
            .set("assignmentId", assignmentId)
            .set("teamId", teamId)
            .expand()
        val cookie = sessionStore.getSessionCookie()
        val request = Request.Builder()
            .url(CLASSCODE_LINK_BUILDER(uri))
            .put(
                objectMapper.writeValueAsString(
                    UpdateRequestStateInput(isJoinTeam = isJoinTeam, creator = creator, requestId = requestId, state = state),
                ).toRequestBody(MEDIA_TYPE),
            )
            .addHeader("Cookie", cookie.first())
            .build()
        return request.send(httpClient) { response ->
            if (response.isSuccessful) {
                Either.Right(value = Unit)
            } else {
                handleSirenResponseClassCode<Unit>(
                    response = response,
                    type = null,
                    jsonMapper = objectMapper,
                )
            }
        }
    }

    override suspend fun deleteTeamInClassCode(
        courseId: Int,
        classroomId: Int,
        assignmentId: Int,
        teamId: Int,
        leaveRequestStateInput: LeaveRequestStateInput,
    ): Either<HandleClassCodeResponseError, Unit> {
        val ensureLink = navigationRepo.ensureLink(key = TEAM_KEY, fetchLink = { bootUpServices.getHome() }) ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
        val uri = UriTemplate.fromTemplate(ensureLink.href)
            .set("courseId", courseId)
            .set("classroomId", classroomId)
            .set("assignmentId", assignmentId)
            .set("teamId", teamId)
            .expand()
        val cookie = sessionStore.getSessionCookie()
        val request = Request.Builder()
            .url(CLASSCODE_LINK_BUILDER(uri))
            .delete(
                objectMapper.writeValueAsString(
                    leaveRequestStateInput,
                ).toRequestBody(MEDIA_TYPE),
            )
            .addHeader("Cookie", cookie.first())
            .build()
        return request.send(httpClient) { response ->
            if (response.isSuccessful) {
                Either.Right(value = Unit)
            } else {
                handleSirenResponseClassCode<Unit>(
                    response = response,
                    type = null,
                    jsonMapper = objectMapper,
                )
            }
        }
    }
}
