package isel.ps.classcode.presentation.classroom.services

import com.damnhandy.uri.template.UriTemplate
import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.ASSIGNMENT_KEY
import isel.ps.classcode.CLASSCODE_LINK_BUILDER
import isel.ps.classcode.CLASSROOM_KEY
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.Team
import isel.ps.classcode.domain.deserialization.ClassCodeClassroomWithAssignmentsDto
import isel.ps.classcode.domain.deserialization.ClassCodeClassroomWithAssignmentsDtoType
import isel.ps.classcode.domain.deserialization.ClassCodeTeacherAssignmentDto
import isel.ps.classcode.domain.deserialization.ClassCodeTeacherAssignmentDtoType
import isel.ps.classcode.http.NavigationRepository
import isel.ps.classcode.http.handleSirenResponseClassCode
import isel.ps.classcode.http.send
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.bootUp.services.BootUpServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Implementation of the [ClassroomServices] interface that will in the real app
 */
class RealClassroomServices(private val sessionStore: SessionStore, private val objectMapper: ObjectMapper, private val httpClient: OkHttpClient, private val navigationRepo: NavigationRepository, private val bootUpServices: BootUpServices): ClassroomServices {
    override suspend fun getAssignments(classroomId: Int, courseId: Int): Either<HandleClassCodeResponseError, List<Assignment>> {
        val ensureLink = navigationRepo.ensureLink(key = CLASSROOM_KEY, fetchLink =  { bootUpServices.getHome() }) ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
        val uri = UriTemplate.fromTemplate(ensureLink.href)
            .set("courseId", courseId)
            .set("classroomId", classroomId)
            .expand()
        val cookie = sessionStore.getSessionCookie()
        val request = Request.Builder()
            .url(CLASSCODE_LINK_BUILDER(uri))
            .addHeader("Cookie", cookie.first())
            .build()
        val result = request.send(httpClient) { response ->
            handleSirenResponseClassCode<ClassCodeClassroomWithAssignmentsDto>(response = response, type = ClassCodeClassroomWithAssignmentsDtoType, jsonMapper = objectMapper)
        }
        return when (result) {
            is Either.Right -> {
                Either.Right(value = result.value.properties.assignments.map { Assignment(classCodeAssignmentDeserialization = it) })
            }
            is Either.Left -> Either.Left(value = result.value)
        }
    }

    override suspend fun getTeams(
        classroomId: Int,
        courseId: Int,
        assignmentId: Int
    ): Either<HandleClassCodeResponseError, List<Team>> {
        val ensureLink = navigationRepo.ensureLink(key = ASSIGNMENT_KEY, fetchLink =  { bootUpServices.getHome() }) ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
        val uri =  UriTemplate.fromTemplate(ensureLink.href)
            .set("courseId", courseId)
            .set("classroomId", classroomId)
            .set("assignmentId", assignmentId)
            .expand()
        val cookie = sessionStore.getSessionCookie()
        val request = Request.Builder()
            .url(CLASSCODE_LINK_BUILDER(uri))
            .addHeader("Cookie", cookie.first())
            .build()
        val result = request.send(httpClient) { response ->
            handleSirenResponseClassCode<ClassCodeTeacherAssignmentDto>(response = response, type = ClassCodeTeacherAssignmentDtoType, jsonMapper = objectMapper)
        }
        return when (result) {
            is Either.Right -> {
                Either.Right(value = result.value.properties.teams.map { Team(classCodeTeamDeserialization = it) })
            }
            is Either.Left -> Either.Left(value = result.value)
        }
    }

}