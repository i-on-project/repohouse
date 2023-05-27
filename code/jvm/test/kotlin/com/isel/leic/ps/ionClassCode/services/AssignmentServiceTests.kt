package com.isel.leic.ps.ionClassCode.services

import com.isel.leic.ps.ionClassCode.domain.Assignment
import com.isel.leic.ps.ionClassCode.domain.Classroom
import com.isel.leic.ps.ionClassCode.domain.Delivery
import com.isel.leic.ps.ionClassCode.domain.Repo
import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.Teacher
import com.isel.leic.ps.ionClassCode.domain.Team
import com.isel.leic.ps.ionClassCode.domain.input.AssignmentInput
import com.isel.leic.ps.ionClassCode.domain.requests.Composite
import com.isel.leic.ps.ionClassCode.domain.requests.CreateRepo
import com.isel.leic.ps.ionClassCode.domain.requests.CreateTeam
import com.isel.leic.ps.ionClassCode.domain.requests.JoinTeam
import com.isel.leic.ps.ionClassCode.http.model.input.AssignmentInputModel
import com.isel.leic.ps.ionClassCode.repository.AssignmentRepository
import com.isel.leic.ps.ionClassCode.repository.ClassroomRepository
import com.isel.leic.ps.ionClassCode.repository.DeliveryRepository
import com.isel.leic.ps.ionClassCode.repository.RepoRepository
import com.isel.leic.ps.ionClassCode.repository.TeamRepository
import com.isel.leic.ps.ionClassCode.repository.UsersRepository
import com.isel.leic.ps.ionClassCode.repository.request.CompositeRepository
import com.isel.leic.ps.ionClassCode.repository.request.CreateRepoRepository
import com.isel.leic.ps.ionClassCode.repository.request.CreateTeamRepository
import com.isel.leic.ps.ionClassCode.repository.request.JoinTeamRepository
import com.isel.leic.ps.ionClassCode.repository.transaction.Transaction
import com.isel.leic.ps.ionClassCode.repository.transaction.TransactionManager
import com.isel.leic.ps.ionClassCode.utils.Result
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.sql.Timestamp
import java.time.Instant

@SpringBootTest
class AssignmentServiceTests {
    @Autowired
    lateinit var assignmentServices: AssignmentServices

    @TestConfiguration
    class Config {
        @Bean
        fun transactionManager(): TransactionManager = object : TransactionManager {
            override fun <R> run(block: (Transaction) -> R): R {
                val mockedTransaction = mock<Transaction> {
                    val mockedAssignmentRepository = mock<AssignmentRepository> {
                        on { getClassroomAssignments(classroomId = 2) } doReturn listOf(ClassroomServiceTests.assignment)
                        on {
                            createAssignment(assignment = AssignmentInput(classroomId = 1, description = "description", title = "title", maxNumberGroups = 2, maxElemsPerGroup = 2, minElemsPerGroup = 1))
                        } doReturn Assignment(id = 1, classroomId = 1, maxElemsPerGroup = 2, maxNumberGroups = 2, releaseDate = Timestamp.from(Instant.now()), description = "description", title = "title", minElemsPerGroup = 1)
                        on { getAssignmentById(assignmentId = 1) } doReturn Assignment(id = 1, classroomId = 1, maxElemsPerGroup = 2, maxNumberGroups = 2, releaseDate = Timestamp.from(Instant.now()), description = "description", title = "title", minElemsPerGroup = 1)
                        on { getAssignmentById(assignmentId = 2) } doReturn Assignment(id = 2, classroomId = 2, maxElemsPerGroup = 2, maxNumberGroups = 2, releaseDate = Timestamp.from(Instant.now()), description = "description2", title = "title2", minElemsPerGroup = 1)
                        on { getAssignmentById(assignmentId = 3) } doReturn Assignment(id = 3, classroomId = 4, maxElemsPerGroup = 2, maxNumberGroups = 2, releaseDate = Timestamp.from(Instant.now()), description = "description3", title = "title3", minElemsPerGroup = 1)
                        on { getAssignmentById(assignmentId = 4) } doReturn Assignment(id = 4, classroomId = 1, maxElemsPerGroup = 2, maxNumberGroups = 2, releaseDate = Timestamp.from(Instant.now()), description = "description4", title = "title4", minElemsPerGroup = 1)
                        on { deleteAssignment(assignmentId = 1) } doAnswer { }
                    }

                    val mockedClassroomRepository = mock<ClassroomRepository> {
                        on { getClassroomById(classroomId = 1) } doReturn Classroom(id = 1, name = "Classroom 1", inviteCode = "inviteLink", isArchived = false, lastSync = Timestamp.from(Instant.now()), courseId = 1, teacherId = 1)
                        on { getClassroomById(classroomId = 2) } doReturn Classroom(id = 2, name = "Classroom 2", inviteCode = "inviteLink1", isArchived = true, lastSync = Timestamp.from(Instant.now()), courseId = 1, teacherId = 1)
                    }

                    val mockedUsersRepository = mock<UsersRepository> {
                        on {
                            getTeacher(teacherId = 1)
                        } doReturn Teacher(name = "teacher2", isCreated = false, githubUsername = "test1234", githubId = 123452, token = "token1", id = 2, email = "test1@alunos.isel.pt")
                        on {
                            getStudent(studentId = 3)
                        } doReturn Student(name = "student3", email = "email3", id = 3, githubUsername = "test1234", githubId = 123452, token = "token1", isCreated = true, schoolId = 48309)
                    }

                    val mockedDeliveriesRepository = mock<DeliveryRepository> {
                        on { getDeliveriesByAssignment(assignmentId = 1) } doReturn listOf()
                        on { getDeliveriesByAssignment(assignmentId = 4) } doReturn listOf(Delivery(id = 1, assignmentId = 1, dueDate = Timestamp.from(Instant.now()), tagControl = "tagControl", lastSync = Timestamp.from(Instant.now())))
                    }

                    val mockedRepoRepository = mock<RepoRepository> {
                        on { getRepoByTeam(teamId = 2) } doReturn Repo(id = 2, name = "repo2", isCreated = false, url = "url")
                        on { getRepoByTeam(teamId = 3) } doReturn Repo(id = 3, name = "repo3", isCreated = false, url = "url")
                        on { getRepoByTeam(teamId = 4) } doReturn Repo(id = 4, name = "repo4", isCreated = false, url = "url")
                    }
                    val mockedCompositeRepository = mock<CompositeRepository> {
                        on { getCompositeRequestsThatAreNotAccepted() } doReturn listOf(Composite(id = 1, composite = null, creator = 1))
                    }
                    val mockedCreateTeamRepository = mock<CreateTeamRepository> {
                        on { getCreateTeamRequestByCompositeId(compositeId = 1) } doReturn CreateTeam(teamId = 1, teamName = "team1", id = 1, composite = 1, creator = 1, githubTeamId = 1)
                    }
                    val mockedJoinTeamRepository = mock<JoinTeamRepository> {
                        on { getJoinTeamRequestByCompositeId(compositeId = 1) } doReturn JoinTeam(githubUsername = "user3", id = 3, composite = 3, creator = 3, teamId = 3)
                    }
                    val mockedCreateRepoRepository = mock<CreateRepoRepository> {
                        on { getCreateRepoRequestByCompositeId(compositeId = 1) } doReturn CreateRepo(repoId = 4, repoName = "repo4", id = 4, composite = 4, creator = 4)
                    }

                    val mockedTeamsRepository = mock<TeamRepository> {
                        on { getTeamsFromAssignment(assignmentId = 1) } doReturn listOf(
                            Team(id = 1, name = "team1", isCreated = false, assignment = 1, isClosed = false),
                        )
                        on { getTeamsFromAssignment(assignmentId = 2) } doReturn listOf(
                            Team(id = 2, name = "team2", isCreated = false, assignment = 2, isClosed = false),
                        )
                        on { getTeamsFromAssignment(assignmentId = 3) } doReturn listOf(
                            Team(id = 3, name = "team3", isCreated = false, assignment = 3, isClosed = false),
                        )
                        on { getTeamsFromAssignment(assignmentId = 4) } doReturn listOf(
                            Team(id = 4, name = "team4", isCreated = false, assignment = 4, isClosed = false),
                        )
                        on { getTeamsFromAssignment(assignmentId = 5) } doReturn listOf(
                            Team(id = 5, name = "team5", isCreated = false, assignment = 5, isClosed = false),
                        )
                        on { getTeamsFromStudent(studentId = 1) } doReturn listOf()
                    }
                    on { classroomRepository } doReturn mockedClassroomRepository
                    on { assignmentRepository } doReturn mockedAssignmentRepository
                    on { usersRepository } doReturn mockedUsersRepository
                    on { deliveryRepository } doReturn mockedDeliveriesRepository
                    on { teamRepository } doReturn mockedTeamsRepository
                    on { repoRepository } doReturn mockedRepoRepository
                    on { joinTeamRepository } doReturn mockedJoinTeamRepository
                    on { createTeamRepository } doReturn mockedCreateTeamRepository
                    on { createRepoRepository } doReturn mockedCreateRepoRepository
                    on { compositeRepository } doReturn mockedCompositeRepository
                }
                return block(mockedTransaction)
            }
        }
    }

    // TEST: createAssignment

    @Test
    fun `createAssignment should give an InternalError because the userId is invalid`() {
        // given: an invalid user id
        val userId = -1

        // when: getting an error because of an invalid user id
        val assignment = assignmentServices.createAssignment(
            userId = userId,
            assignmentInfo = AssignmentInputModel(
                classroomId = 1,
                description = "description",
                title = "title",
                maxNumberGroups = 2,
                minNumberElems = 1,
                maxNumberElems = 2,
                dueDate = Timestamp.from(Instant.now()),
            ),
        )

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createAssignment should give an InvalidInput because the classroomId is invalid`() {
        // given: an invalid classroom id
        val classroomId = -1

        // when: getting an error because of an invalid classroom id
        val assignment = assignmentServices.createAssignment(
            userId = 1,
            assignmentInfo = AssignmentInputModel(
                classroomId = classroomId,
                description = "description",
                title = "title",
                maxNumberGroups = 2,
                minNumberElems = 1,
                maxNumberElems = 2,
                dueDate = Timestamp.from(Instant.now()),
            ),
        )

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createAssignment should give an InvalidInput because the description is invalid`() {
        // given: an invalid description
        val description = ""

        // when: getting an error because of an invalid description
        val assignment = assignmentServices.createAssignment(
            userId = 1,
            assignmentInfo = AssignmentInputModel(
                classroomId = 1,
                description = description,
                title = "title",
                maxNumberGroups = 2,
                minNumberElems = 0,
                maxNumberElems = 2,
                dueDate = Timestamp.from(Instant.now()),
            ),
        )

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createAssignment should give an InvalidInput because the title is invalid`() {
        // given: an invalid title
        val title = ""

        // when: getting an error because of an invalid title
        val assignment = assignmentServices.createAssignment(
            userId = 1,
            assignmentInfo = AssignmentInputModel(
                classroomId = 1,
                description = "description",
                title = title,
                maxNumberGroups = 2,
                minNumberElems = 0,
                maxNumberElems = 2,
                dueDate = Timestamp.from(Instant.now()),
            ),
        )

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createAssignment should give an InvalidInput because the max number groups is invalid`() {
        // given: an invalid max number groups
        val maxNumberGroups = -1

        // when: getting an error because of an invalid max number groups
        val assignment = assignmentServices.createAssignment(
            userId = 1,
            assignmentInfo = AssignmentInputModel(
                classroomId = 1,
                description = "description",
                title = "title",
                maxNumberGroups = maxNumberGroups,
                minNumberElems = 0,
                maxNumberElems = 2,
                dueDate = Timestamp.from(Instant.now()),
            ),
        )

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createAssignment should give an InvalidInput because the max number elems is invalid`() {
        // given: an invalid max number groups
        val maxNumberElems = -1

        // when: getting an error because of an invalid max number elems
        val assignment = assignmentServices.createAssignment(
            userId = 1,
            assignmentInfo = AssignmentInputModel(
                classroomId = 1,
                description = "description",
                title = "title",
                maxNumberGroups = 2,
                minNumberElems = 0,
                maxNumberElems = maxNumberElems,
                dueDate = Timestamp.from(Instant.now()),
            ),
        )

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createAssignment should give an InternalError because user id is not in database`() {
        // given: a valid user id
        val userId = 4

        // when: getting an error because the user id is not in database
        val assignment = assignmentServices.createAssignment(
            userId = 4,
            assignmentInfo = AssignmentInputModel(
                classroomId = userId,
                description = "description",
                title = "title",
                maxNumberGroups = 2,
                minNumberElems = 1,
                maxNumberElems = 2,
                dueDate = Timestamp.from(Instant.now()),
            ),
        )

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createAssignment should give an ClassroomNotFound because classroom id is not in database`() {
        // given: a valid classroom id
        val classroomId = 4

        // when: getting an error because the classroom id is not in database
        val assignment = assignmentServices.createAssignment(
            userId = 1,
            assignmentInfo = AssignmentInputModel(
                classroomId = classroomId,
                description = "description",
                title = "title",
                maxNumberGroups = 2,
                minNumberElems = 1,
                maxNumberElems = 2,
                dueDate = Timestamp.from(Instant.now()),
            ),
        )

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createAssignment should give an ClassroomArchived because classroom is already archived`() {
        // given: a valid classroom id
        val classroomId = 2

        // when: getting an error because the classroom is already archived
        val assignment = assignmentServices.createAssignment(
            userId = 1,
            assignmentInfo = AssignmentInputModel(
                classroomId = classroomId,
                description = "description",
                title = "title",
                maxNumberGroups = 2,
                minNumberElems = 1,
                maxNumberElems = 2,
                dueDate = Timestamp.from(Instant.now()),
            ),
        )

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.ClassroomArchived)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createAssignment should give a assignment`() {
        // when: getting an error because the classroom id is not in database
        val assignment = assignmentServices.createAssignment(
            userId = 1,
            assignmentInfo = AssignmentInputModel(
                classroomId = 1,
                description = "description",
                title = "title",
                maxNumberGroups = 2,
                minNumberElems = 1,
                maxNumberElems = 2,
                dueDate = Timestamp.from(Instant.now()),
            ),
        )

        if (assignment is Result.Success) {
            assert(assignment.value.description == "description")
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: getAssigmentInfo

    @Test
    fun `getStudentAssigmentInfo should give an AssignmentNotFound because assignment id is invalid`() {
        // given: a invalid assignment id
        val assignmentId = -1

        // when: getting an error because the assignment id is invalid
        val assignment = assignmentServices.getTeacherAssignmentInfo(assignmentId = assignmentId)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getAssigmentInfo should give an AssignmentNotFound because assignment id is not in database`() {
        // given: a valid assignment id
        val assignmentId = 10

        // when: getting an error because the assignment id is not in database
        val assignment = assignmentServices.getTeacherAssignmentInfo(assignmentId = assignmentId)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getAssigmentInfo should give a assignment`() {
        // given: a valid assignment id
        val assignmentId = 1

        // when: getting the assignment
        val assignment = assignmentServices.getTeacherAssignmentInfo(assignmentId = assignmentId)

        if (assignment is Result.Success) {
            assert(assignment.value.assignment.id == assignmentId)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // test: getStudentAssignmentInfo

    @Test
    fun `getStudentAssignmentInfo should give an AssignmentNotFound because assignment id is invalid`() {
        // given: a invalid assignment id
        val assignmentId = -1

        // when: getting an error because the assignment id is invalid
        val assignment = assignmentServices.getStudentAssignmentInfo(assignmentId = assignmentId, studentId = 3)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getStudentAssignmentInfo should give an AssignmentNotFound because assignment id is not in database`() {
        // given: a valid assignment id
        val assignmentId = 10

        // when: getting an error because the assignment id is not in database
        val assignment = assignmentServices.getStudentAssignmentInfo(assignmentId = assignmentId, studentId = 3)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getStudentAssignmentInfo should give a assignment`() {
        // given: a valid assignment id
        val assignmentId = 1

        // when: getting the assignment
        val assignment = assignmentServices.getStudentAssignmentInfo(assignmentId = assignmentId, studentId = 3)

        if (assignment is Result.Success) {
            assert(assignment.value.assignment.id == assignmentId)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: deleteAssignment

    @Test
    fun `deleteAssignment should give an AssignmentNotFound because assignment id is invalid`() {
        // given: a invalid assignment id
        val assignmentId = -1

        // when: getting an error because the assignment id is invalid
        val assignment = assignmentServices.deleteAssignment(assignmentId = assignmentId)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `deleteAssignment should give an AssignmentNotFound because assignment id is not in database`() {
        // given: a valid assignment id
        val assignmentId = 10

        // when: getting an error because the assignment id is not in database
        val assignment = assignmentServices.deleteAssignment(assignmentId = assignmentId)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `deleteAssignment should give an ClassroomNotFound the classroom was not found`() {
        // given: a valid assignment id
        val assignmentId = 3

        // when: getting an error because the classroom id is not in database
        val assignment = assignmentServices.deleteAssignment(assignmentId = assignmentId)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `deleteAssignment should give an ClassroomArchived the classroom was already archived`() {
        // given: a valid assignment id
        val assignmentId = 2

        // when: getting an error because the classroom is archived
        val assignment = assignmentServices.deleteAssignment(assignmentId = assignmentId)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.ClassroomArchived)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `deleteAssignment should give an AssignmentNotDeleted the classroom was deliveries so cannot be deleted`() {
        // given: a valid assignment id
        val assignmentId = 4

        // when: getting an error because the assignment still have deliveries
        val assignment = assignmentServices.deleteAssignment(assignmentId = assignmentId)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.AssignmentNotDeleted)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `deleteAssignment should delete the assignment`() {
        // given: a valid assignment id
        val assignmentId = 1

        // when: getting a true because the assignment was deleted
        val assignment = assignmentServices.deleteAssignment(assignmentId = assignmentId)

        if (assignment is Result.Success) {
            assert(assignment.value)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: getAssignmentStudentTeams

    @Test
    fun `getAssignmentStudentTeams should give an AssignmentNotFound because assignment id is invalid`() {
        // given: a invalid assignment id
        val assignmentId = -1

        // when: getting an error because the assignment id is invalid
        val assignment = assignmentServices.getAssignmentStudentTeams(assignmentId = assignmentId, studentId = 3)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getAssignmentStudentTeams should give an InternalError because student id is invalid`() {
        // given: a invalid student id
        val studentId = -1

        // when: getting an error because the student id is invalid
        val assignment = assignmentServices.getAssignmentStudentTeams(assignmentId = 1, studentId = studentId)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getAssignmentStudentTeams should give an AssignmentNotFound because assignment id is not in database`() {
        // given: a valid assignment id
        val assignmentId = 10

        // when: getting a list of teams
        val assignment = assignmentServices.getAssignmentStudentTeams(assignmentId = assignmentId, studentId = 3)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getAssignmentStudentTeams should give a list teams`() {
        // when: getting an error because the assignment id is not in database
        val assignment = assignmentServices.getAssignmentStudentTeams(assignmentId = 1, studentId = 3)

        if (assignment is Result.Success) {
            assert(assignment.value.isEmpty())
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: getTeacherAssignmentInfoTeams

    @Test
    fun `getTeacherAssignmentInfoTeams should give an AssignmentNotFound because assignmed id is invalid`() {
        // given: a valid assignment id
        val assignmentId = -1

        // when: getting an error
        val assignment = assignmentServices.getTeacherAssignmentInfoTeams(assignmentId = assignmentId)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getTeacherAssignmentInfoTeams should give an AssignmentNotFound because assignmed id is not in database`() {
        // given: a valid assignment id
        val assignmentId = 10

        // when: getting an error
        val assignment = assignmentServices.getTeacherAssignmentInfoTeams(assignmentId = assignmentId)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `can get the list of teams should be as success`() {
        // given: a valid assignment id
        val assignmentId = 4

        // when: getting an error
        val assignment = assignmentServices.getTeacherAssignmentInfoTeams(assignmentId = assignmentId)

        if (assignment is Result.Success) {
            assert(assignment.value.createTeamComposites.isNotEmpty())
        } else {
            fail("Should not be Either.Right")
        }
    }
}
