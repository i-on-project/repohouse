package isel.ps.classcode.presentation.classroom.services

import com.damnhandy.uri.template.UriTemplate
import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.CLASSCODE_LINK_BUILDER
import isel.ps.classcode.MEDIA_TYPE
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.domain.ArchiveRepo
import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.CreateTeamComposite
import isel.ps.classcode.domain.GetAssignmentsResponse
import isel.ps.classcode.domain.LeaveClassroomRequest
import isel.ps.classcode.domain.Team
import isel.ps.classcode.domain.Teams
import isel.ps.classcode.domain.UpdateArchiveRepoInput
import isel.ps.classcode.domain.UpdateCreateTeamStatusInput
import isel.ps.classcode.domain.UpdateLeaveClassroomCompositeInput
import isel.ps.classcode.domain.deserialization.ClassCodeClassroomWithArchiveRequestsDto
import isel.ps.classcode.domain.deserialization.ClassCodeClassroomWithArchiveRequestsDtoType
import isel.ps.classcode.domain.deserialization.ClassCodeCourseWithLeaveCourseRequestsDtoType
import isel.ps.classcode.domain.deserialization.ClassCodeTeacherAssignmentDto
import isel.ps.classcode.domain.deserialization.ClassCodeTeacherAssignmentDtoType
import isel.ps.classcode.http.NavigationRepository
import isel.ps.classcode.http.handleSirenResponseClassCode
import isel.ps.classcode.http.send
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.ASSIGNMENT_KEY
import isel.ps.classcode.presentation.CLASSROOM_ARCHIVED_KEY
import isel.ps.classcode.presentation.CLASSROOM_KEY
import isel.ps.classcode.presentation.CREATE_TEAM_KEY
import isel.ps.classcode.presentation.LEAVE_CLASSROOM_KEY
import isel.ps.classcode.presentation.bootUp.services.BootUpServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Implementation of the [ClassroomServices] interface that will in the real app
 */
class RealClassroomServices(private val sessionStore: SessionStore, private val objectMapper: ObjectMapper, private val httpClient: OkHttpClient, private val navigationRepo: NavigationRepository, private val bootUpServices: BootUpServices) : ClassroomServices {
    override suspend fun getAssignments(
        classroomId: Int,
        courseId: Int,
    ): Either<HandleClassCodeResponseError, GetAssignmentsResponse> {
        val ensureLink =
            navigationRepo.ensureLink(key = CLASSROOM_KEY, fetchLink = { bootUpServices.getHome() })
                ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
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
            handleSirenResponseClassCode<ClassCodeClassroomWithArchiveRequestsDto>(
                response = response,
                type = ClassCodeClassroomWithArchiveRequestsDtoType,
                jsonMapper = objectMapper,
            )
        }
        return when (result) {
            is Either.Right -> {
                Either.Right(
                    value = GetAssignmentsResponse(
                        assignments = result.value.properties.classroomModel.assignments.map {
                            Assignment(classCodeAssignmentDeserialization = it)
                        },
                        archiveRepos = result.value.properties.archiveRequest?.map {
                            ArchiveRepo(deserialization = it)
                        },
                        leaveClassroomsRequests = result.value.properties.leaveClassrooms.map {
                            LeaveClassroomRequest(deserialization = it)
                        },
                    ),
                )
            }

            is Either.Left -> Either.Left(value = result.value)
        }
    }

    override suspend fun getTeams(
        classroomId: Int,
        courseId: Int,
        assignmentId: Int,
    ): Either<HandleClassCodeResponseError, Teams> {
        val ensureLink = navigationRepo.ensureLink(
            key = ASSIGNMENT_KEY,
            fetchLink = { bootUpServices.getHome() },
        )
            ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
        val uri = UriTemplate.fromTemplate(ensureLink.href)
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
            handleSirenResponseClassCode<ClassCodeTeacherAssignmentDto>(
                response = response,
                type = ClassCodeTeacherAssignmentDtoType,
                jsonMapper = objectMapper,
            )
        }
        return when (result) {
            is Either.Right -> {
                val teamsCreated =
                    result.value.properties.teamsCreated.map { Team(classCodeTeamDeserialization = it) }
                val createTeamComposite = result.value.properties.createTeamComposites.map {
                    CreateTeamComposite(deserialization = it)
                }
                Either.Right(
                    value = Teams(
                        teamsCreated = teamsCreated,
                        createTeamComposite = createTeamComposite,
                    ),
                )
            }

            is Either.Left -> Either.Left(value = result.value)
        }
    }

    override suspend fun changeCreateTeamStatus(
        classroomId: Int,
        courseId: Int,
        assignmentId: Int,
        teamId: Int,
        updateCreateTeamStatus: UpdateCreateTeamStatusInput,
    ): Either<HandleClassCodeResponseError, Unit> {
        val ensureLink = navigationRepo.ensureLink(key = CREATE_TEAM_KEY, fetchLink = { bootUpServices.getHome() }) ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
        val uri = UriTemplate.fromTemplate(ensureLink.href)
            .set("courseId", courseId)
            .set("classroomId", classroomId)
            .set("assignmentId", assignmentId)
            .set("teamId", teamId)
            .expand()
        val cookie = sessionStore.getSessionCookie()
        val request = Request.Builder()
            .url(CLASSCODE_LINK_BUILDER(uri))
            .post(
                objectMapper.writeValueAsString(
                    updateCreateTeamStatus,
                ).toRequestBody(MEDIA_TYPE),
            )
            .addHeader("Cookie", cookie.first())
            .build()
        val result = request.send(httpClient) { response ->
            if (response.isSuccessful) {
                Either.Right(value = Unit)
            } else {
                handleSirenResponseClassCode(
                    response = response,
                    type = null,
                    jsonMapper = objectMapper,
                )
            }
        }
        return when (result) {
            is Either.Right -> Either.Right(value = Unit)

            is Either.Left -> Either.Left(value = result.value)
        }
    }

    override suspend fun changeStatusArchiveRepoInClassCode(courseId: Int, classroomId: Int, updateArchiveRepo: UpdateArchiveRepoInput): Either<HandleClassCodeResponseError, Unit> {
        val ensureLink = navigationRepo.ensureLink(key = CLASSROOM_ARCHIVED_KEY, fetchLink = { bootUpServices.getHome() }) ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
        val cookie = sessionStore.getSessionCookie()
        val uri = UriTemplate.fromTemplate(ensureLink.href)
            .set("courseId", courseId)
            .set("classroomId", classroomId)
            .expand()
        val request = Request.Builder()
            .url(CLASSCODE_LINK_BUILDER(uri))
            .post(
                objectMapper.writeValueAsString(
                    updateArchiveRepo,
                ).toRequestBody(MEDIA_TYPE),
            )
            .addHeader("Cookie", cookie.first())
            .build()
        return request.send(httpClient) { response ->
            handleSirenResponseClassCode<Unit>(
                response = response,
                type = null,
                jsonMapper = objectMapper,
                ignoreBody = true,
            )
        }
    }

    override suspend fun updateLeaveClassroomCompositeInClassCode(input: UpdateLeaveClassroomCompositeInput, courseId: Int, userId: Int): Either<HandleClassCodeResponseError, Unit> {
        val ensureLink = navigationRepo.ensureLink(key = LEAVE_CLASSROOM_KEY, fetchLink = { bootUpServices.getHome() }) ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
        val uri = UriTemplate.fromTemplate(ensureLink.href)
            .set("courseId", courseId)
            .set("classroomId", input.leaveClassroom.classroomId)
            .set("userId", userId)
            .expand()
        val cookie = sessionStore.getSessionCookie()
        val request = Request.Builder()
            .url(CLASSCODE_LINK_BUILDER(uri))
            .post(
                objectMapper.writeValueAsString(
                    input,
                ).toRequestBody(MEDIA_TYPE),
            )
            .addHeader("Cookie", cookie.first())
            .build()
        return request.send(httpClient) { response ->
            handleSirenResponseClassCode<Unit>(response = response, type = ClassCodeCourseWithLeaveCourseRequestsDtoType, jsonMapper = objectMapper, ignoreBody = true)
        }
    }
}
