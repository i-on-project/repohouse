package isel.ps.classcode.presentation.classroom.services

import isel.ps.classcode.domain.Team
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.delay

class FakeClassroomServices : ClassroomServices {
    override suspend fun getTeams(classroomId: Int): Either<HandleClassCodeResponseError, List<Team>> {
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