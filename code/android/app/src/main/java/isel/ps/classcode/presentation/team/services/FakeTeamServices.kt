package isel.ps.classcode.presentation.team.services

import isel.ps.classcode.domain.LeaveRequestStateInput
import isel.ps.classcode.domain.TeamRequests
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.utils.Either

class FakeTeamServices : TeamServices {
    override suspend fun getTeamRequests(
        courseId: Int,
        classroomId: Int,
        assignmentId: Int,
        teamId: Int,
    ): Either<HandleClassCodeResponseError, TeamRequests> {
        TODO("Not yet implemented")
    }

    override suspend fun updateStateOfRequestInClassCode(
        courseId: Int,
        classroomId: Int,
        assignmentId: Int,
        teamId: Int,
        creator: Int,
        requestId: Int,
        state: String,
        isJoinTeam: Boolean,
    ): Either<HandleClassCodeResponseError, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTeamInClassCode(
        courseId: Int,
        classroomId: Int,
        assignmentId: Int,
        teamId: Int,
        leaveRequestStateInput: LeaveRequestStateInput,
    ): Either<HandleClassCodeResponseError, Unit> {
        TODO("Not yet implemented")
    }
}
