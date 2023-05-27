package isel.ps.classcode.presentation.course.services

import isel.ps.classcode.domain.Classroom
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.delay
import java.sql.Timestamp
import java.time.Instant

/**
 * Implementation of the [CourseServices] interface that will be used for tests
 */
class FakeCourseServices : CourseServices {
    override suspend fun getClassrooms(courseId: Int): Either<HandleClassCodeResponseError, List<Classroom>> {
        delay(2000)
        return Either.Right(
            value = List(10) { index ->
                val i = index + 1
                Classroom(
                    id = i,
                    name = "Classroom $i",
                    lastSync = Timestamp(Instant.now().toEpochMilli()),
                    inviteCode = "invite link $i",
                    isArchived = i in (6..7),
                    courseId = courseId,
                )
            },
        )
    }
}
