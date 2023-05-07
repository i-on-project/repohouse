package isel.ps.classcode.presentation.classroom.services

import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.Team
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

interface ClassroomServices {
    suspend fun getAssignments(classroomId: Int, courseId: Int): Either<HandleClassCodeResponseError, List<Assignment>>
    suspend fun getTeams(classroomId: Int, courseId: Int, assignmentId: Int): Either<HandleClassCodeResponseError, List<Team>>
}