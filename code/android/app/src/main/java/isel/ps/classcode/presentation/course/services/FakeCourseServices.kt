package isel.ps.classcode.presentation.course.services

import isel.ps.classcode.domain.UpdateLeaveCourseCompositeInput
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

/**
 * Implementation of the [CourseServices] interface that will be used for tests
 */
class FakeCourseServices : CourseServices {
    override suspend fun getClassrooms(courseId: Int): Either<HandleClassCodeResponseError, ClassroomsAndLeaveCourseRequests> {
        TODO("Not yet implemented")
    }

    override suspend fun updateLeaveCourseCompositeInClassCode(
        input: UpdateLeaveCourseCompositeInput,
        userId: Int,
    ): Either<HandleClassCodeResponseError, Unit> {
        TODO("Not yet implemented")
    }
}
