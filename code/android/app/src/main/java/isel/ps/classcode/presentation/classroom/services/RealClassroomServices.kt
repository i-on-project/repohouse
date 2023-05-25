package isel.ps.classcode.presentation.classroom.services

import com.damnhandy.uri.template.UriTemplate
import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.ASSIGNMENT_KEY
import isel.ps.classcode.CLASSCODE_LINK_BUILDER
import isel.ps.classcode.CLASSROOM_KEY
import isel.ps.classcode.CREATE_TEAM_KEY
import isel.ps.classcode.GITHUB_ADD_MEMBER_TO_TEAM
import isel.ps.classcode.GITHUB_ADD_TEAM
import isel.ps.classcode.GITHUB_CREATE_REPO
import isel.ps.classcode.MEDIA_TYPE
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.CreateTeamComposite
import isel.ps.classcode.domain.CreateRepo
import isel.ps.classcode.domain.Team
import isel.ps.classcode.domain.Teams
import isel.ps.classcode.domain.UpdateCreateTeamStatusInput
import isel.ps.classcode.domain.deserialization.ClassCodeClassroomWithAssignmentsDto
import isel.ps.classcode.domain.deserialization.ClassCodeClassroomWithAssignmentsDtoType
import isel.ps.classcode.domain.deserialization.ClassCodeTeacherAssignmentDto
import isel.ps.classcode.domain.deserialization.ClassCodeTeacherAssignmentDtoType
import isel.ps.classcode.domain.deserialization.GitHubCreateRepoDeserialization
import isel.ps.classcode.domain.deserialization.GitHubCreateTeamDeserialization
import isel.ps.classcode.http.NavigationRepository
import isel.ps.classcode.http.handleResponseGitHub
import isel.ps.classcode.http.handleSirenResponseClassCode
import isel.ps.classcode.http.send
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.bootUp.services.BootUpServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Implementation of the [ClassroomServices] interface that will in the real app
 */
class RealClassroomServices(private val sessionStore: SessionStore, private val objectMapper: ObjectMapper, private val httpClient: OkHttpClient, private val navigationRepo: NavigationRepository, private val bootUpServices: BootUpServices): ClassroomServices {
    override suspend fun getAssignments(
        classroomId: Int,
        courseId: Int
    ): Either<HandleClassCodeResponseError, List<Assignment>> {
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
            handleSirenResponseClassCode<ClassCodeClassroomWithAssignmentsDto>(
                response = response,
                type = ClassCodeClassroomWithAssignmentsDtoType,
                jsonMapper = objectMapper
            )
        }
        return when (result) {
            is Either.Right -> {
                Either.Right(value = result.value.properties.assignments.map {
                    Assignment(
                        classCodeAssignmentDeserialization = it
                    )
                })
            }

            is Either.Left -> Either.Left(value = result.value)
        }
    }

    override suspend fun getTeams(
        classroomId: Int,
        courseId: Int,
        assignmentId: Int
    ): Either<HandleClassCodeResponseError, Teams> {
        val ensureLink = navigationRepo.ensureLink(
            key = ASSIGNMENT_KEY,
            fetchLink = { bootUpServices.getHome() })
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
                jsonMapper = objectMapper
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
                        createTeamComposite = createTeamComposite
                    )
                )
            }

            is Either.Left -> Either.Left(value = result.value)
        }
    }

    override suspend fun createTeamInGitHub(
        createTeamComposite: CreateTeamComposite,
        orgName: String
    ): ResultFromRequest<Int> {
        val accessToken = sessionStore.getGithubToken().first()
        val requestCreateTeam = Request.Builder()
            .url(GITHUB_ADD_TEAM(orgName))
            .post(
                objectMapper.writeValueAsString(
                    mapOf(
                        "name" to createTeamComposite.createTeam.teamName,
                    )
                ).toRequestBody(MEDIA_TYPE)
            )
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        val result = requestCreateTeam.send(httpClient) { response ->
            handleResponseGitHub<GitHubCreateTeamDeserialization>(
                response = response,
                jsonMapper = objectMapper
            )
        }
        return when (result) {
            is Either.Left -> ResultFromRequest(isCompleted = false)
            is Either.Right -> ResultFromRequest(isCompleted = true, value = result.value.id)
        }
    }
    override suspend fun addMemberToTeamInGitHub(
        orgName: String,
        teamSlug: String,
        username: String
    ): ResultFromRequest<Unit> {
        val accessToken = sessionStore.getGithubToken().first()
        val requestCreateTeam = Request.Builder()
            .url(GITHUB_ADD_MEMBER_TO_TEAM(orgName, teamSlug, username))
            .put(
                objectMapper.writeValueAsString(
                    emptyMap<String, String>()
                ).toRequestBody(MEDIA_TYPE)
            )
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        return requestCreateTeam.send(httpClient) { response ->
            ResultFromRequest(isCompleted = response.isSuccessful)
        }
    }

    override suspend fun createRepoInGitHub(orgName: String, teamId: Int?, repo: CreateRepo): ResultFromRequest<String> {
        if (teamId == null) return ResultFromRequest(isCompleted = false)
        val accessToken = sessionStore.getGithubToken().first()
        val requestCreateTeam = Request.Builder()
            .url(GITHUB_CREATE_REPO(orgName))
            .post(
                objectMapper.writeValueAsString(
                    mapOf(
                        "name" to repo.repoName,
                        "team_id" to teamId,
                    )
                ).toRequestBody(MEDIA_TYPE)
            )
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        val result = requestCreateTeam.send(httpClient) { response ->
            handleResponseGitHub<GitHubCreateRepoDeserialization>(
                response = response,
                jsonMapper = objectMapper
            )
        }
        return when (result) {
            is Either.Right -> ResultFromRequest<String>(isCompleted = true , value = result.value.htmlUrl)
            is Either.Left -> ResultFromRequest<String>(isCompleted = false )
        }
    }

    override suspend fun changeCreateTeamStatus(
        classroomId: Int,
        courseId: Int,
        assignmentId: Int,
        teamId: Int,
        updateCreateTeamStatus: UpdateCreateTeamStatusInput
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
                    updateCreateTeamStatus
                ).toRequestBody(MEDIA_TYPE)
            )
            .addHeader("Cookie", cookie.first())
            .build()
        val result = request.send(httpClient) { response ->
            if (response.isSuccessful) {
                Either.Right(value = Unit)
            } else {
                handleSirenResponseClassCode<Unit>(
                    response = response,
                    type = ClassCodeTeacherAssignmentDtoType,
                    jsonMapper = objectMapper
                )
            }
        }
        return when (result) {
            is Either.Right -> {
                Either.Right(value = Unit)
            }

            is Either.Left -> Either.Left(value = result.value)
        }
    }
}

data class ResultFromRequest<T> (val isCompleted: Boolean, val value: T? = null)