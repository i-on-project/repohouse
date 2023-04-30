package isel.ps.classcode.presentation.course.services

import isel.ps.classcode.domain.Classroom
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

interface CourseServices {
    suspend fun getCourse(courseId: Int): Either<HandleClassCodeResponseError, List<Classroom>>
}