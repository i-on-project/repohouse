package isel.ps.classcode.presentation.login.services

import android.app.Activity
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either


/**
 * Represents the services that can be used on the login
 */
interface LoginServices {
    suspend fun getTheAccessToken(code: String, state: String): Either<HandleClassCodeResponseError, Unit>
    suspend fun startOauth(activity: Activity) :Either<HandleClassCodeResponseError, Unit>
}