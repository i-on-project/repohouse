package isel.ps.classcode.presentation.course.services

import isel.ps.classcode.domain.Classroom
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

/**
 * Interface defining the services available for the Course feature.
 */
interface CourseServices {
    suspend fun getClassrooms(courseId: Int): Either<HandleClassCodeResponseError, List<Classroom>>
}
