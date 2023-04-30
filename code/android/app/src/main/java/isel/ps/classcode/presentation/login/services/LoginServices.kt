package isel.ps.classcode.presentation.login.services

import isel.ps.classcode.domain.AuthInfo
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleRedirectClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either


/**
 * Represents the services that can be used on the login
 */
interface LoginServices {
    suspend fun auth(): Either<HandleClassCodeResponseError, RequestInfo>
    suspend fun tradeAndStoreAccessToken(code: String, stateCookie: String, state: String): Either<HandleRedirectClassCodeResponseError, AuthInfo>
}