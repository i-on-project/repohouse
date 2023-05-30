package isel.ps.classcode.presentation.login.services

import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

/**
 * Represents the services that can be used on the login
 */
interface LoginServices {
    suspend fun getTheAccessToken(code: String, state: String): Either<HandleClassCodeResponseError, Unit>
    suspend fun startOauth(startActivity: (String, String) -> Boolean): Either<HandleClassCodeResponseError, Unit>
}
