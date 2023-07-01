package isel.ps.classcode.presentation.menu.services

import isel.ps.classcode.FakeDataStorage
import isel.ps.classcode.domain.Course
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

/**
 * Implementation of the [MenuServices] interface that will be used for tests
 */
class FakeMenuServices(private val data: FakeDataStorage) : MenuServices {
    override suspend fun getCourses(): Either<HandleClassCodeResponseError, List<Course>> {
        return Either.Right(value = data.getCourses())
    }

    override suspend fun logout() {
        // Do nothing
    }
}
