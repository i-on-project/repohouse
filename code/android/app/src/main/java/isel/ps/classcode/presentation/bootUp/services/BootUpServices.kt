package isel.ps.classcode.presentation.bootUp.services

import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

interface BootUpServices {
    suspend fun getHome(): Either<HandleClassCodeResponseError, Unit>
}
