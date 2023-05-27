package com.isel.leic.ps.ionClassCode.services

import com.isel.leic.ps.ionClassCode.domain.Assignment
import com.isel.leic.ps.ionClassCode.domain.Classroom
import com.isel.leic.ps.ionClassCode.domain.Feedback
import com.isel.leic.ps.ionClassCode.domain.Repo
import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.Team
import com.isel.leic.ps.ionClassCode.domain.input.FeedbackInput
import com.isel.leic.ps.ionClassCode.domain.input.RepoInput
import com.isel.leic.ps.ionClassCode.domain.input.TeamInput
import com.isel.leic.ps.ionClassCode.domain.input.request.CompositeInput
import com.isel.leic.ps.ionClassCode.domain.input.request.CreateRepoInput
import com.isel.leic.ps.ionClassCode.domain.input.request.CreateTeamInput
import com.isel.leic.ps.ionClassCode.domain.input.request.JoinTeamInput
import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveTeamInput
import com.isel.leic.ps.ionClassCode.domain.requests.Composite
import com.isel.leic.ps.ionClassCode.domain.requests.CreateRepo
import com.isel.leic.ps.ionClassCode.domain.requests.CreateTeam
import com.isel.leic.ps.ionClassCode.domain.requests.JoinTeam
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveTeam
import com.isel.leic.ps.ionClassCode.domain.requests.Request
import com.isel.leic.ps.ionClassCode.repository.AssignmentRepository
import com.isel.leic.ps.ionClassCode.repository.ClassroomRepository
import com.isel.leic.ps.ionClassCode.repository.FeedbackRepository
import com.isel.leic.ps.ionClassCode.repository.OutboxRepository
import com.isel.leic.ps.ionClassCode.repository.RepoRepository
import com.isel.leic.ps.ionClassCode.repository.TeamRepository
import com.isel.leic.ps.ionClassCode.repository.UsersRepository
import com.isel.leic.ps.ionClassCode.repository.request.CompositeRepository
import com.isel.leic.ps.ionClassCode.repository.request.CreateRepoRepository
import com.isel.leic.ps.ionClassCode.repository.request.CreateTeamRepository
import com.isel.leic.ps.ionClassCode.repository.request.JoinTeamRepository
import com.isel.leic.ps.ionClassCode.repository.request.LeaveTeamRepository
import com.isel.leic.ps.ionClassCode.repository.request.RequestRepository
import com.isel.leic.ps.ionClassCode.repository.transaction.Transaction
import com.isel.leic.ps.ionClassCode.repository.transaction.TransactionManager
import com.isel.leic.ps.ionClassCode.utils.Result
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.sql.Timestamp
import java.time.Instant

@SpringBootTest
class TeamServicesTests {

    @Autowired
    lateinit var teamServices: TeamServices

    @TestConfiguration
    class Config {
        @Bean
        fun transactionManager(): TransactionManager = object : TransactionManager {
            override fun <R> run(block: (Transaction) -> R): R {
                val mockedTransaction = mock<Transaction> {
                    val mockedTeamRepository = mock<TeamRepository> {
                        on { getTeamById(id = 1) } doReturn Team(id = 1, name = "Team1", isCreated = false, assignment = 1, isClosed = false)
                        on { getStudentsFromTeam(teamId = 1) } doReturn listOf(Student(name = "test1245", email = "test@alunos.isel.pt", githubUsername = "test1a23", token = "token5", githubId = 124345, isCreated = false, id = 3, schoolId = null))
                        on { createTeam(team = TeamInput(name = "Classroom2 - 1", 1, false)) } doReturn Team(id = 1, name = "Team1", isCreated = false, assignment = 1, isClosed = false)
                    }
                    val mockedRepoRepository = mock<RepoRepository> {
                        on { getRepoByTeam(teamId = 1) } doReturn Repo(id = 1, name = "Repo1", url = "url", isCreated = false)
                        on { createRepo(repo = RepoInput(name = "Classroom2 - 1 - 1", url = null, teamId = 1)) } doReturn Repo(id = 1, name = "Classroom2 - 1 - 1", url = null, isCreated = false)
                    }

                    val mockedFeedbackRepository = mock<FeedbackRepository> {
                        on { getFeedbacksByTeam(teamId = 1) } doReturn listOf()
                        on { createFeedback(feedback = FeedbackInput(description = "description", label = "label", teamId = 1)) } doReturn Feedback(id = 1, description = "description", label = "label", teamId = 1)
                    }

                    val mockedClassroomRepository = mock<ClassroomRepository> {
                        on { getClassroomById(classroomId = 1) } doReturn Classroom(id = 1, name = "Classroom1", lastSync = Timestamp.from(Instant.now()), isArchived = true, inviteCode = "inviteLink", courseId = 1, teacherId = 1)
                        on { getClassroomById(classroomId = 2) } doReturn Classroom(id = 1, name = "Classroom2", lastSync = Timestamp.from(Instant.now()), isArchived = false, inviteCode = "inviteLink1", courseId = 1, teacherId = 1)
                    }

                    val mockedCompositeRepository = mock<CompositeRepository> {
                        on { createCompositeRequest(request = CompositeInput(composite = null), creator = 1) } doReturn Composite(id = 1, creator = 1)
                    }

                    val mockedCreateTeamRepository = mock<CreateTeamRepository> {
                        on { createCreateTeamRequest(request = CreateTeamInput(teamId = 1, composite = 1, teamName = "Team1"), creator = 1) } doReturn CreateTeam(id = 1, creator = 1, composite = 1, teamId = 1, githubTeamId = 1, teamName = "team1")
                        on { createCreateTeamRequest(request = CreateTeamInput(teamId = 2, composite = 2, teamName = "team2"), creator = 2) } doReturn CreateTeam(id = 2, creator = 2, composite = 2, teamId = 2, githubTeamId = 2, teamName = "team2")
                    }

                    val mockedJoinTeamRepoRepository = mock<JoinTeamRepository> {
                        on { createJoinTeamRequest(request = JoinTeamInput(assignmentId = 2, teamId = 1, creatorGitHubUserName = "user"), creator = 1) } doReturn JoinTeam(id = 1, creator = 1, teamId = 1, githubUsername = "name")
                        on { getJoinTeamRequests() } doReturn listOf()
                    }

                    val mockedLeaveTeamRepository = mock<LeaveTeamRepository> {
                        on { createLeaveTeamRequest(request = LeaveTeamInput(teamId = 1), creator = 1) } doReturn LeaveTeam(id = 1, creator = 1, teamId = 1, githubUsername = "name")
                        on { getLeaveTeamRequests() } doReturn listOf()
                    }

                    val mockedAssignmentRepository = mock<AssignmentRepository> {
                        on { getAssignmentById(assignmentId = 1) } doReturn Assignment(id = 1, classroomId = 1, maxNumberGroups = 2, maxElemsPerGroup = 2, description = "description", title = "title", releaseDate = Timestamp.from(Instant.now()), minElemsPerGroup = 0)
                        on { getAssignmentById(assignmentId = 2) } doReturn Assignment(id = 2, classroomId = 2, maxNumberGroups = 2, maxElemsPerGroup = 2, description = "description2", title = "title2", releaseDate = Timestamp.from(Instant.now()), minElemsPerGroup = 0)
                        on { getAssignmentById(assignmentId = 3) } doReturn Assignment(id = 3, classroomId = 3, maxNumberGroups = 2, maxElemsPerGroup = 2, description = "description3", title = "title3", releaseDate = Timestamp.from(Instant.now()), minElemsPerGroup = 0)
                    }

                    val mockedRequestRepository = mock<RequestRepository> {
                        on { getRequestById(id = 1) } doReturn Request(id = 1, creator = 1, composite = null, state = "Rejected")
                        on { getRequestById(id = 2) } doReturn Request(id = 2, creator = 1, composite = null)
                    }

                    val mockedCreateRepoRepository = mock<CreateRepoRepository> {
                        on { createCreateRepoRequest(request = CreateRepoInput(repoId = 1, composite = 1, repoName = "repo"), creator = 2) } doReturn CreateRepo(id = 1, creator = 2, repoId = 1, composite = 1, repoName = "repo")
                    }
                    val mockedOutboxRepository = mock<OutboxRepository> {}
                    val mockedUsersRepository = mock<UsersRepository> {
                        on { getStudent(studentId = 1) } doReturn Student(
                            name = "student2",
                            token = "token3",
                            githubId = 1234152,
                            githubUsername = "test12345",
                            isCreated = false,
                            email = "test3@alunos.isel.pt",
                            id = 1,
                            schoolId = 1235,
                        )
                    }
                    on { teamRepository } doReturn mockedTeamRepository
                    on { repoRepository } doReturn mockedRepoRepository
                    on { feedbackRepository } doReturn mockedFeedbackRepository
                    on { classroomRepository } doReturn mockedClassroomRepository
                    on { compositeRepository } doReturn mockedCompositeRepository
                    on { createTeamRepository } doReturn mockedCreateTeamRepository
                    on { joinTeamRepository } doReturn mockedJoinTeamRepoRepository
                    on { leaveTeamRepository } doReturn mockedLeaveTeamRepository
                    on { assignmentRepository } doReturn mockedAssignmentRepository
                    on { requestRepository } doReturn mockedRequestRepository
                    on { createRepoRepository } doReturn mockedCreateRepoRepository
                    on { outboxRepository } doReturn mockedOutboxRepository
                    on { usersRepository } doReturn mockedUsersRepository
                }
                return block(mockedTransaction)
            }
        }
    }

    // TEST: getTeamInfo

    @Test
    fun `getTeamInfo should give an TeamNotFound because the teamId is invalid`() {
        // given: an invalid team id
        val teamId = -1

        // when: getting an error because of an invalid team id
        val team = teamServices.getTeamInfo(teamId = teamId)

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.TeamNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getTeamInfo should give an TeamNotFound because the teamId is not in database`() {
        // given: a valid team id
        val teamId = 5

        // when: getting an error because the team id is not in database
        val team = teamServices.getTeamInfo(teamId = teamId)

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.TeamNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getTeamInfo should give a the team info`() {
        // given: a valid team id
        val teamId = 1

        // when: getting the team info
        val team = teamServices.getTeamInfo(teamId = teamId)

        if (team is Result.Success) {
            assert(team.value.team.name == "Team1")
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: createTeamRequest

    @Test
    fun `createTeamRequest should give an InternalError because the creator is invalid`() {
        // given: an invalid creator
        val creator = -1

        // when: getting an error because of an invalid creator
        val team = teamServices.createTeamRequest(
            creator = creator,
            assignmentId = 1,
            classroomId = 1,
            creatorGitHubUserName = "user",
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeamRequest should give an AssignmentNotFound because the assignmentId is invalid`() {
        // given: an invalid assignment id
        val assignmentId = -1

        // when: getting an error because of an invalid assignment id
        val team = teamServices.createTeamRequest(
            assignmentId = assignmentId,
            classroomId = 1,
            creator = 1,
            creatorGitHubUserName = "user",
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeamRequest should give an ClassroomNotFound because the classroomId is invalid`() {
        // given: an invalid classroom id
        val classroomId = -1

        // when: getting an error because of an invalid classroom id
        val team = teamServices.createTeamRequest(
            assignmentId = 1,
            classroomId = classroomId,
            creator = 1,
            creatorGitHubUserName = "user",
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeamRequest should give an ClassroomNotFound because the classroomId is not in database`() {
        // given: a valid classroom id
        val classroomId = 3

        // when: getting an error because the classroom id is not in database
        val team = teamServices.createTeamRequest(
            assignmentId = 1,
            classroomId = classroomId,
            creator = 1,
            creatorGitHubUserName = "user",
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeamRequest should give an ClassroomArchived because the classroomId is already archived`() {
        // given: a valid classroom id
        val classroomId = 1

        // when: getting an error because the classroom id is archived
        val team = teamServices.createTeamRequest(
            assignmentId = 1,
            classroomId = classroomId,
            creator = 1,
            creatorGitHubUserName = "user",
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.ClassroomArchived)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeamRequest should give the id of the request`() {
        // when: getting a success
        val team = teamServices.createTeamRequest(
            assignmentId = 1,
            classroomId = 2,
            creator = 1,
            creatorGitHubUserName = "team1",
        )

        if (team is Result.Success) {
            assert(team.value.id == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: leaveTeamRequest

    @Test
    fun `leaveTeamRequest should give an InternalError because the creator is invalid`() {
        // given: an invalid creator
        val creator = -1

        // when: getting an error because of a creator
        val team = teamServices.leaveTeamRequest(
            leaveInfo = LeaveTeamInput(
                composite = null,
                teamId = 1,
            ),
            creator = creator,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `leaveTeamRequest should give an InvalidData because the composite is invalid`() {
        // given: an invalid composite
        val composite = -1

        // when: getting an error because of a composite creator
        val team = teamServices.leaveTeamRequest(
            leaveInfo = LeaveTeamInput(
                composite = composite,
                teamId = 1,
            ),
            creator = 1,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `leaveTeamRequest should give an InvalidData because the teamId is invalid`() {
        // given: an invalid team id
        val teamId = -1

        // when: getting an error because of a composite creator
        val team = teamServices.leaveTeamRequest(
            leaveInfo = LeaveTeamInput(
                composite = null,
                teamId = teamId,
            ),
            creator = 1,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.TeamNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `leaveTeamRequest should give an TeamNotFound because the teamId is not in database`() {
        // given: a valid team id
        val teamId = 3

        // when: getting an error because the teamId is not in database
        val team = teamServices.leaveTeamRequest(
            leaveInfo = LeaveTeamInput(
                composite = null,
                teamId = teamId,
            ),
            creator = 1,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.TeamNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `leaveTeamRequest should give the id do request`() {
        // when: getting an error because the teamId is not in database
        val team = teamServices.leaveTeamRequest(
            leaveInfo = LeaveTeamInput(
                composite = null,
                teamId = 1,
            ),
            creator = 1,
        )

        if (team is Result.Success) {
            assert(team.value.id == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: joinTeamRequest

    @Test
    fun `joinTeamRequest should give an AssignmentNotFound because the assignmentId is invalid`() {
        // given: an invalid assignment id
        val assignmentId = -1

        // when: getting an error because of an assignment id
        val team = teamServices.joinTeamRequest(
            joinInfo = JoinTeamInput(
                composite = null,
                teamId = 1,
                assignmentId = assignmentId,
                creatorGitHubUserName = "user",
            ),
            creator = 1,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `joinTeamRequest should give an TeamNotFound because the teamId is invalid`() {
        // given: an invalid team id
        val teamId = -1

        // when: getting an error because of a team id
        val team = teamServices.joinTeamRequest(
            joinInfo = JoinTeamInput(
                composite = null,
                teamId = teamId,
                assignmentId = 2,
                creatorGitHubUserName = "user",
            ),
            creator = 1,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.TeamNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `joinTeamRequest should give an InternalError because the creator is invalid`() {
        // given: an invalid creator
        val creator = -1

        // when: getting an error because of a creator
        val team = teamServices.joinTeamRequest(
            joinInfo = JoinTeamInput(
                composite = null,
                teamId = 1,
                assignmentId = 1,
                creatorGitHubUserName = "user",
            ),
            creator = creator,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `joinTeamRequest should give an AssignmentNotFound because the assignment id is not in database`() {
        // given: a valid assignment id
        val assignmentId = 4

        // when: getting an error because of an assignment id is not in db
        val team = teamServices.joinTeamRequest(
            joinInfo = JoinTeamInput(
                composite = null,
                teamId = 1,
                assignmentId = assignmentId,
                creatorGitHubUserName = "user",
            ),
            creator = 1,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `joinTeamRequest should give an ClassroomNotFound because the classroom id is not in database`() {
        // given: a valid assignment id
        val assignmentId = 3

        // when: getting an error because of an classroom id is not in database
        val team = teamServices.joinTeamRequest(
            joinInfo = JoinTeamInput(
                composite = null,
                teamId = 1,
                assignmentId = assignmentId,
                creatorGitHubUserName = "user",
            ),
            creator = 1,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `joinTeamRequest should give an ClassroomArchived because the classroom is archived`() {
        // given: a valid assignment id
        val assignmentId = 1

        // when: getting an error because of a classroom the classroom is archived
        val team = teamServices.joinTeamRequest(
            joinInfo = JoinTeamInput(
                composite = null,
                teamId = 1,
                assignmentId = assignmentId,
                creatorGitHubUserName = "user",
            ),
            creator = 1,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.ClassroomArchived)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `joinTeamRequest should give an TeamNotFound because the team was not found`() {
        // given: a valid assignment id
        val teamId = 3

        // when: getting an error because of a classroom the team was not found
        val team = teamServices.joinTeamRequest(
            joinInfo = JoinTeamInput(
                composite = null,
                teamId = teamId,
                assignmentId = 2,
                creatorGitHubUserName = "user",
            ),
            creator = 1,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.TeamNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `joinTeamRequest should give the id of the request`() {
        // given: a valid assignment id
        val teamId = 1

        // when: getting an error because of a classroom the team was not found
        val team = teamServices.joinTeamRequest(
            joinInfo = JoinTeamInput(
                composite = null,
                teamId = teamId,
                assignmentId = 2,
                creatorGitHubUserName = "user",
            ),
            creator = 1,
        )

        if (team is Result.Success) {
            assert(team.value.id == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: updateTeamRequestStatus

    @Test
    fun `updateTeamRequestStatus should give an RequestNotFound because the requestId is invalid`() {
        // given: an invalid request id
        val requestId = -1

        // when: getting an error because of an invalid request id
        val team = teamServices.updateTeamRequestStatus(
            requestId = requestId,
            teamId = 1,
            classroomId = 2,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.RequestNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateTeamRequestStatus should give an TeamNotFound because the teamId is invalid`() {
        // given: an invalid team id
        val teamId = -1

        // when: getting an error because of an invalid team id
        val team = teamServices.updateTeamRequestStatus(
            requestId = 1,
            teamId = teamId,
            classroomId = 2,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.TeamNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateTeamRequestStatus should give an ClassroomNotFound because the classroomId is invalid`() {
        // given: an invalid classroom id
        val classroomId = -1

        // when: getting an error because of an invalid classroom id
        val team = teamServices.updateTeamRequestStatus(
            requestId = 1,
            teamId = 1,
            classroomId = classroomId,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateTeamRequestStatus should give an ClassroomNotFound because the classroomId is valid but not in database`() {
        // given: an invalid classroom id
        val classroomId = 3

        // when: getting an error because of an invalid classroom id
        val team = teamServices.updateTeamRequestStatus(
            requestId = 1,
            teamId = 1,
            classroomId = classroomId,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateTeamRequestStatus should give an ClassroomArchived because the classroom is already archived`() {
        // given: an invalid classroom id
        val classroomId = 1

        // when: getting an error because of an invalid classroom id
        val team = teamServices.updateTeamRequestStatus(
            requestId = 1,
            teamId = 1,
            classroomId = classroomId,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.ClassroomArchived)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateTeamRequestStatus should give an TeamNotFound because the teamId was not in database`() {
        // given: an invalid team id
        val teamId = 3

        // when: getting an error because the team d was not in database
        val team = teamServices.updateTeamRequestStatus(
            requestId = 1,
            teamId = teamId,
            classroomId = 2,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.TeamNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateTeamRequestStatus should give an RequestNotFound because the requestId was not in database`() {
        // given: an invalid request id
        val requestId = 3

        // when: getting an error because the request id was not in database
        val team = teamServices.updateTeamRequestStatus(
            requestId = requestId,
            teamId = 1,
            classroomId = 2,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.RequestNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateTeamRequestStatus should give an RequestNotRejected because the request was not rejected`() {
        // given: an invalid request id
        val requestId = 2

        // when: getting an error because the request was not rejected
        val team = teamServices.updateTeamRequestStatus(
            requestId = requestId,
            teamId = 1,
            classroomId = 2,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.RequestNotRejected)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateTeamRequestStatus should update the status`() {
        // when: getting the flag was updated
        val team = teamServices.updateTeamRequestStatus(
            requestId = 1,
            teamId = 1,
            classroomId = 2,
        )

        if (team is Result.Success) {
            assert(team.value)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: postFeedback

    @Test
    fun `postFeedback should give a TeamNotFound because the teamId is invalid`() {
        // given: an invalid team id
        val teamId = -1

        // when: getting an error because of an invalid team id
        val team = teamServices.postFeedback(
            feedbackInfo = FeedbackInput(
                teamId = teamId,
                description = "description",
                label = "label",
            ),
            classroomId = 2,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.TeamNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `postFeedback should give an InvalidData because the description is invalid`() {
        // given: an invalid description
        val description = ""

        // when: getting an error because of an invalid description
        val team = teamServices.postFeedback(
            feedbackInfo = FeedbackInput(
                teamId = 1,
                description = description,
                label = "label",
            ),
            classroomId = 1,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `postFeedback should give an InvalidData because the label is invalid`() {
        // given: an invalid label
        val label = ""

        // when: getting an error because of an invalid label
        val team = teamServices.postFeedback(
            feedbackInfo = FeedbackInput(
                teamId = 1,
                description = "",
                label = label,
            ),
            classroomId = 1,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `postFeedback should give an ClassroomNotFound because the classroomId is invalid`() {
        // given: an invalid classroom id
        val classroomId = -1

        // when: getting an error because of an invalid classroom id
        val team = teamServices.postFeedback(
            feedbackInfo = FeedbackInput(
                teamId = 1,
                description = "description",
                label = "label",
            ),
            classroomId = classroomId,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `postFeedback should give an ClassroomNotFound because the classroomId is not in database`() {
        // given: an invalid classroom id
        val classroomId = 3

        // when: getting an error because the classroom id is not in database
        val team = teamServices.postFeedback(
            feedbackInfo = FeedbackInput(
                teamId = 1,
                description = "description",
                label = "label",
            ),
            classroomId = classroomId,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `postFeedback should give an ClassroomArchived because the classroom is archived`() {
        // given: an invalid classroom id
        val classroomId = 1

        // when: getting an error because the classroom is archived
        val team = teamServices.postFeedback(
            feedbackInfo = FeedbackInput(
                teamId = 1,
                description = "description",
                label = "label",
            ),
            classroomId = classroomId,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.ClassroomArchived)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `postFeedback should give an TeamNotFound because the team was not found`() {
        // given: an invalid classroom id
        val teamId = 3

        // when: getting an error because the team was not found
        val team = teamServices.postFeedback(
            feedbackInfo = FeedbackInput(
                teamId = teamId,
                description = "description",
                label = "label",
            ),
            classroomId = 2,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.TeamNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `postFeedback should give the new feedback`() {
        // when: getting an error because the team was not found
        val team = teamServices.postFeedback(
            feedbackInfo = FeedbackInput(
                teamId = 1,
                description = "description",
                label = "label",
            ),
            classroomId = 2,
        )

        if (team is Result.Success) {
            assert(team.value.id == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: getTeamRequests

    @Test
    fun `getTeamsRequests should give a TeamNotFound because the teamId is invalid`() {
        // given: an invalid team id
        val teamId = -1

        // when: getting an error because of an invalid team id
        val team = teamServices.getTeamsRequests(
            teamId = teamId,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.TeamNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getTeamsRequests should give a TeamNotFound because the teamId is not in the database`() {
        // given: an invalid team id
        val teamId = 3

        // when: getting an error because the team was not found
        val team = teamServices.getTeamsRequests(
            teamId = teamId,
        )

        if (team is Result.Problem) {
            assert(team.value is TeamServicesError.TeamNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getTeamsRequests should give the team and their requests`() {
        // given: a valid team id
        val teamId = 1

        // when: getting an error because the team was not found
        val team = teamServices.getTeamsRequests(
            teamId = teamId,
        )

        if (team is Result.Success) {
            assert(team.value.team.name == "Team1")
        } else {
            fail("Should not be Either.Right")
        }
    }
}
