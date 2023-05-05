package isel.ps.classcode.presentation.login.services

import isel.ps.classcode.domain.AuthInfo
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleRedirectClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

class FakeLoginServices: LoginServices {

    override suspend fun getTheTokens(
        code: String,
        githubId: String
    ): Either<HandleClassCodeResponseError, Unit> {
        return Either.Right(Unit)
    }


}