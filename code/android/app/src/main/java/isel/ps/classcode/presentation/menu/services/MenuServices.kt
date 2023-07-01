package isel.ps.classcode.presentation.menu.services

import isel.ps.classcode.domain.Course
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

/**
 * Represents the services that can be used on the menu
 */
interface MenuServices {
    suspend fun getCourses(): Either<HandleClassCodeResponseError, List<Course>>
    suspend fun logout()
}
