package isel.ps.classcode.presentation.classroom.services

import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.domain.Team
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either
import okhttp3.OkHttpClient

class RealClassroomServices(private val sessionStore: SessionStore, private val objectMapper: ObjectMapper, private val httpClient: OkHttpClient): ClassroomServices {
    override suspend fun getTeams(classroomId: Int): Either<HandleClassCodeResponseError, List<Team>> {
        TODO("Not yet implemented")
    }

}