package isel.ps.classcode.presentation.course.services

import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.domain.Classroom
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either
import okhttp3.OkHttpClient

class RealCourseServices(private val sessionStore: SessionStore, private val objectMapper: ObjectMapper, private val httpClient: OkHttpClient) : CourseServices {
    override suspend fun getClassrooms(courseId: Int): Either<HandleClassCodeResponseError, List<Classroom>> {
        TODO("Not yet implemented")
    }
}