package isel.ps.classcode.presentation.bootUp.services

import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

class FakeBootUpServices : BootUpServices {
    override suspend fun getHome(): Either<HandleClassCodeResponseError, Unit> {
        return Either.Right(value = Unit)
    }
}
