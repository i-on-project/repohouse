package isel.ps.classcode.presentation.classroom.services

import isel.ps.classcode.domain.Team
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

interface ClassroomServices {
    suspend fun getTeams(classroomId: Int): Either<HandleClassCodeResponseError, List<Team>>

}