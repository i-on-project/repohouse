package isel.ps.classcode.presentation.course.services

import com.damnhandy.uri.template.UriTemplate
import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.CLASSCODE_LINK_BUILDER
import isel.ps.classcode.COURSE_KEY
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.domain.Classroom
import isel.ps.classcode.domain.deserialization.ClassCodeCourseDto
import isel.ps.classcode.domain.deserialization.ClassCodeCourseDtoType
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
 * Implementation of the [CourseServices] interface that will be in the real app
 */

class RealCourseServices(private val sessionStore: SessionStore, private val objectMapper: ObjectMapper, private val httpClient: OkHttpClient, private val navigationRepo: NavigationRepository, private val bootUpServices: BootUpServices) : CourseServices {
    override suspend fun getClassrooms(courseId: Int): Either<HandleClassCodeResponseError, List<Classroom>> {
        val ensureLink = navigationRepo.ensureLink(key = COURSE_KEY, fetchLink =  { bootUpServices.getHome() }) ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
        val uri =  UriTemplate.fromTemplate(ensureLink.href)
            .set("courseId", courseId).expand()
        val cookie = sessionStore.getSessionCookie()
        val request = Request.Builder()
            .url(CLASSCODE_LINK_BUILDER(uri))
            .addHeader("Cookie", cookie.first())
            .build()
        val result = request.send(httpClient) { response ->
            handleSirenResponseClassCode<ClassCodeCourseDto>(response = response, type = ClassCodeCourseDtoType, jsonMapper = objectMapper)
        }
        return when (result) {
            is Either.Right -> {
                Either.Right(value = result.value.properties.classrooms.map { Classroom(classCodeClassroomDeserialization = it) })
            }
            is Either.Left -> Either.Left(value = result.value)
        }
    }
}