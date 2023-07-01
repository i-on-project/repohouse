package isel.ps.classcode.presentation.course.services

import com.damnhandy.uri.template.UriTemplate
import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.CLASSCODE_LINK_BUILDER
import isel.ps.classcode.presentation.COURSE_KEY
import isel.ps.classcode.presentation.LEAVE_COURSE_KEY
import isel.ps.classcode.MEDIA_TYPE
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.domain.Classroom
import isel.ps.classcode.domain.LeaveCourseRequest
import isel.ps.classcode.domain.UpdateLeaveCourseCompositeInput
import isel.ps.classcode.domain.deserialization.ClassCodeCourseWithLeaveCourseRequestsDto
import isel.ps.classcode.domain.deserialization.ClassCodeCourseWithLeaveCourseRequestsDtoType
import isel.ps.classcode.http.NavigationRepository
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
 * Implementation of the [CourseServices] interface that will be in the real app
 */

data class ClassroomsAndLeaveCourseRequests(
    val classrooms: List<Classroom>,
    val leaveCourseRequests: List<LeaveCourseRequest>,
)

class RealCourseServices(private val sessionStore: SessionStore, private val objectMapper: ObjectMapper, private val httpClient: OkHttpClient, private val navigationRepo: NavigationRepository, private val bootUpServices: BootUpServices) : CourseServices {
    override suspend fun getClassrooms(courseId: Int): Either<HandleClassCodeResponseError, ClassroomsAndLeaveCourseRequests> {
        val ensureLink = navigationRepo.ensureLink(key = COURSE_KEY, fetchLink = { bootUpServices.getHome() }) ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
        val uri = UriTemplate.fromTemplate(ensureLink.href)
            .set("courseId", courseId).expand()
        val cookie = sessionStore.getSessionCookie()
        val request = Request.Builder()
            .url(CLASSCODE_LINK_BUILDER(uri))
            .addHeader("Cookie", cookie.first())
            .build()
        val result = request.send(httpClient) { response ->
            handleSirenResponseClassCode<ClassCodeCourseWithLeaveCourseRequestsDto>(response = response, type = ClassCodeCourseWithLeaveCourseRequestsDtoType, jsonMapper = objectMapper)
        }
        return when (result) {
            is Either.Right -> {
                val classrooms = result.value.properties.course.classrooms.map { Classroom(classCodeClassroomDeserialization = it) }
                val leaveCourseRequests = result.value.properties.leaveCourseRequests.map { LeaveCourseRequest(deserialization = it) }
                Either.Right(value = ClassroomsAndLeaveCourseRequests(classrooms = classrooms, leaveCourseRequests = leaveCourseRequests))
            }
            is Either.Left -> Either.Left(value = result.value)
        }
    }

    override suspend fun updateLeaveCourseCompositeInClassCode(input: UpdateLeaveCourseCompositeInput, userId: Int): Either<HandleClassCodeResponseError, Unit> {
        val ensureLink = navigationRepo.ensureLink(key = LEAVE_COURSE_KEY, fetchLink = { bootUpServices.getHome() }) ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
        val uri = UriTemplate.fromTemplate(ensureLink.href)
            .set("courseId", input.leaveCourse.courseId)
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
