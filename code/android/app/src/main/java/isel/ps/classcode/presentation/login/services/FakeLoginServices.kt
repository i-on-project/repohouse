package isel.ps.classcode.presentation.login.services

import android.app.Activity
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

/**
 * Implementation of the [LoginServices] interface that will be used for tests
 */
class FakeLoginServices: LoginServices {

    override suspend fun getTheAccessToken(
        code: String,
        state: String
    ): Either<HandleClassCodeResponseError, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun startOauth(activity: Activity): Either<HandleClassCodeResponseError, Unit> {
        TODO("Not yet implemented")
    }


}