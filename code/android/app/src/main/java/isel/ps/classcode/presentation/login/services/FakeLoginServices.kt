package isel.ps.classcode.presentation.login.services

import android.content.Context
import android.content.Intent
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.login.LoginActivity
import isel.ps.classcode.presentation.utils.Either

/**
 * Implementation of the [LoginServices] interface that will be used for tests
 */
class FakeLoginServices(private val activity: Context) : LoginServices {

    override suspend fun getTheAccessToken(
        code: String,
        state: String,
    ): Either<HandleClassCodeResponseError, Unit> {
        return Either.Right(value = Unit)
    }

    override suspend fun startOauth(startActivity: (String, String) -> Boolean): Either<HandleClassCodeResponseError, Unit> {
        val intent = Intent(activity, LoginActivity::class.java).apply {
            data = data?.buildUpon()?.appendQueryParameter("code", "code")?.appendQueryParameter("state", "state")?.build()
        }
        activity.startActivity(intent)
        return Either.Right(value = Unit)
    }
}
