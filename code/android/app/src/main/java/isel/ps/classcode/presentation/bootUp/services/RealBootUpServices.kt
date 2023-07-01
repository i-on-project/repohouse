package isel.ps.classcode.presentation.bootUp.services

import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.CLASSCODE_HOME
import isel.ps.classcode.domain.deserialization.ClassCodeHomeDto
import isel.ps.classcode.domain.deserialization.ClassCodeHomeType
import isel.ps.classcode.http.NavigationRepository
import isel.ps.classcode.http.handleSirenResponseClassCode
import isel.ps.classcode.http.send
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.ASSIGNMENT_KEY
import isel.ps.classcode.presentation.AUTH_KEY
import isel.ps.classcode.presentation.CLASSROOM_ARCHIVED_KEY
import isel.ps.classcode.presentation.CLASSROOM_KEY
import isel.ps.classcode.presentation.COURSE_KEY
import isel.ps.classcode.presentation.CREATE_TEAM_KEY
import isel.ps.classcode.presentation.CREDITS_KEY
import isel.ps.classcode.presentation.HOME_KEY
import isel.ps.classcode.presentation.LEAVE_COURSE_KEY
import isel.ps.classcode.presentation.MENU_KEY
import isel.ps.classcode.presentation.REQUESTS_NOT_ACCEPTED_KEY
import isel.ps.classcode.presentation.TEAM_KEY
import isel.ps.classcode.presentation.TOKEN_KEY
import isel.ps.classcode.presentation.utils.Either
import okhttp3.OkHttpClient
import okhttp3.Request

class RealBootUpServices(private val httpClient: OkHttpClient, private val objectMapper: ObjectMapper, private val navigationRepo: NavigationRepository) : BootUpServices {
    override suspend fun getHome(): Either<HandleClassCodeResponseError, Unit> {
        val request = Request.Builder()
            .url(CLASSCODE_HOME)
            .build()
        val result = request.send(httpClient) { response ->
            handleSirenResponseClassCode<ClassCodeHomeDto>(
                response = response,
                type = ClassCodeHomeType,
                jsonMapper = objectMapper,
            )
        }
        return when (result) {
            is Either.Right -> {
                navigationRepo.addLinks(systemLinkKeys, result.value.links)
                Either.Right(value = Unit)
            }

            is Either.Left -> Either.Left(value = result.value)
        }
    }

    private val systemLinkKeys: List<String> = listOf(
        HOME_KEY,
        CREDITS_KEY,
        AUTH_KEY,
        TOKEN_KEY,
        MENU_KEY,
        COURSE_KEY,
        LEAVE_COURSE_KEY,
        CLASSROOM_KEY,
        ASSIGNMENT_KEY,
        TEAM_KEY,
        CREATE_TEAM_KEY,
        REQUESTS_NOT_ACCEPTED_KEY,
        CLASSROOM_ARCHIVED_KEY,
    )
}
