package isel.ps.classcode.presentation.menu.services

import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.CLASSCODE_LINK_BUILDER
import isel.ps.classcode.presentation.MENU_KEY
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.domain.Course
import isel.ps.classcode.domain.deserialization.ClassCodeMenuDto
import isel.ps.classcode.domain.deserialization.ClassCodeMenuDtoType
import isel.ps.classcode.http.NavigationRepository
import isel.ps.classcode.http.handleSirenResponseClassCode
import isel.ps.classcode.http.send
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.bootUp.services.RealBootUpServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Implementation of the [MenuServices] interface that will be used for the real app
 */

class RealMenuServices(private val sessionStore: SessionStore, private val objectMapper: ObjectMapper, private val httpClient: OkHttpClient, private val navigationRepo: NavigationRepository, private val bootUpServices: RealBootUpServices) : MenuServices {
    override suspend fun getCourses(): Either<HandleClassCodeResponseError, List<Course>> {
        val uri = navigationRepo.ensureLink(key = MENU_KEY, fetchLink = { bootUpServices.getHome() }) ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
        val cookie = sessionStore.getSessionCookie()
        val request = Request.Builder()
            .url(CLASSCODE_LINK_BUILDER(uri.href))
            .addHeader("Cookie", cookie.first())
            .build()
        val result = request.send(httpClient) { response ->
            handleSirenResponseClassCode<ClassCodeMenuDto>(response = response, type = ClassCodeMenuDtoType, jsonMapper = objectMapper)
        }
        return when (result) {
            is Either.Right -> {
                Either.Right(value = result.value.properties.courses.map { Course(classCodeCourseWithoutClassroomsDeserialization = it) })
            }
            is Either.Left -> Either.Left(value = result.value)
        }
    }

    override suspend fun logout() {
        sessionStore.cleanTokens()
    }
}
