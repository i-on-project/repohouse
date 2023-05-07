package isel.ps.classcode.presentation.classroom.services

import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.Team
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.delay
import java.sql.Timestamp
import java.time.Instant

class FakeClassroomServices : ClassroomServices {
    override suspend fun getAssignments(classroomId: Int, courseId: Int): Either<HandleClassCodeResponseError, List<Assignment>> {
        delay(2000)
        return Either.Right(value = List(10) { index ->
            val i = index + 1
            Assignment(
                id = i,
                classroomId = classroomId,
                maxElemsPerGroup = 2,
                maxNumberGroups = 2,
                releaseDate = Timestamp(Instant.now().toEpochMilli()),
                description = "Description $i",
                title = "Title $i",
            )},
        )
    }

    override suspend fun getTeams(
        classroomId: Int,
        courseId: Int,
        assignmentId: Int
    ): Either<HandleClassCodeResponseError, List<Team>> {
        delay(2000)
        return Either.Right(value = List(10) { index ->
            val i = index + 1
            Team(
                id = i,
                name = "Team $i",
                assignment = 1,
                isCreated = i !in (6..7),
            )},
        )
    }
}