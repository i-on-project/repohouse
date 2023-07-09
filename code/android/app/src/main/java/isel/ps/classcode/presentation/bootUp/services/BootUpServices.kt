package isel.ps.classcode.presentation.bootUp.services

import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

/**
 * Interface defining the services available for the BootUp feature.
 */
interface BootUpServices {
    suspend fun getHome(): Either<HandleClassCodeResponseError, Unit>
}
