package isel.ps.classcode.presentation.classroom.services

import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.CreateTeamComposite
import isel.ps.classcode.domain.RepoNotCreated
import isel.ps.classcode.domain.Team
import isel.ps.classcode.domain.Teams
import isel.ps.classcode.domain.UpdateCreateTeamStatusInput
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.delay
import java.sql.Timestamp
import java.time.Instant

/**
 * Implementation of the [ClassroomServices] interface that will be used for tests
 */
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
    ): Either<HandleClassCodeResponseError, Teams> {
        delay(2000)
        TODO()
    }

    override suspend fun createTeamInGitHub(
        createTeamComposite: CreateTeamComposite,
        orgName: String
    ): ResultFromRequest<Int> {
        TODO("Not yet implemented")
    }




    override suspend fun addMemberToTeamInGitHub(
        orgName: String,
        teamSlug: String,
        username: String
    ): ResultFromRequest<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun createRepoInGitHub(
        orgName: String,
        teamId: Int?,
        repo: RepoNotCreated
    ): ResultFromRequest<String> {
        TODO("Not yet implemented")
    }

    override suspend fun changeCreateTeamStatus(
        classroomId: Int,
        courseId: Int,
        assignmentId: Int,
        teamId: Int,
        updateCreateTeamStatus: UpdateCreateTeamStatusInput
    ): Either<HandleClassCodeResponseError, Unit> {
        TODO("Not yet implemented")
    }

}