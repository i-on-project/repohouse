package isel.ps.classcode.presentation.login.services

import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

/**
 * Implementation of the [LoginServices] interface that will be used for tests
 */
class FakeLoginServices: LoginServices {

    override suspend fun getTheTokens(
        code: String,
        githubId: String
    ): Either<HandleClassCodeResponseError, Unit> {
        return Either.Right(Unit)
    }


}