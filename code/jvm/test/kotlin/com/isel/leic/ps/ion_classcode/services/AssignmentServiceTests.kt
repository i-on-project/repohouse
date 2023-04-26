package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.Assignment
import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.Delivery
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.input.AssignmentInput
import com.isel.leic.ps.ion_classcode.http.model.input.AssignmentInputModel
import com.isel.leic.ps.ion_classcode.repository.AssignmentRepository
import com.isel.leic.ps.ion_classcode.repository.ClassroomRepository
import com.isel.leic.ps.ion_classcode.repository.DeliveryRepository
import com.isel.leic.ps.ion_classcode.repository.TeamRepository
import com.isel.leic.ps.ion_classcode.repository.UsersRepository
import com.isel.leic.ps.ion_classcode.repository.transaction.Transaction
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Result
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
                        on { getAssignmentsByClassroom(classroomId = 2) } doReturn listOf(ClassroomServiceTests.assignment)
                        on {
                            createAssignment(assignment = AssignmentInput(classroomId = 1, description = "description", title = "title", maxNumberGroups = 2, maxElemsPerGroup = 2))
                        } doReturn Assignment(id = 1, classroomId = 1, maxElemsPerGroup = 2, maxNumberGroups = 2, releaseDate = Timestamp.from(Instant.now()), description = "description", title = "title")
                        on { getAssignmentById(assignmentId = 1) } doReturn Assignment(id = 1, classroomId = 1, maxElemsPerGroup = 2, maxNumberGroups = 2, releaseDate = Timestamp.from(Instant.now()), description = "description", title = "title")
                        on { getAssignmentById(assignmentId = 2) } doReturn Assignment(id = 2, classroomId = 2, maxElemsPerGroup = 2, maxNumberGroups = 2, releaseDate = Timestamp.from(Instant.now()), description = "description2", title = "title2")
                        on { getAssignmentById(assignmentId = 3) } doReturn Assignment(id = 3, classroomId = 4, maxElemsPerGroup = 2, maxNumberGroups = 2, releaseDate = Timestamp.from(Instant.now()), description = "description3", title = "title3")
                        on { getAssignmentById(assignmentId = 4) } doReturn Assignment(id = 4, classroomId = 1, maxElemsPerGroup = 2, maxNumberGroups = 2, releaseDate = Timestamp.from(Instant.now()), description = "description4", title = "title4")
                        on { deleteAssignment(assignmentId = 1) } doAnswer { }
                    }

                    val mockedClassroomRepository = mock<ClassroomRepository> {
                        on { getClassroomById(classroomId = 1) } doReturn Classroom(id = 1, name = "Classroom 1", inviteLink = "inviteLink", isArchived = false, lastSync = Timestamp.from(Instant.now()), courseId = 1)
                        on { getClassroomById(classroomId = 2) } doReturn Classroom(id = 2, name = "Classroom 2", inviteLink = "inviteLink1", isArchived = true, lastSync = Timestamp.from(Instant.now()), courseId = 1)
                    }

                    val mockedUsersRepository = mock<UsersRepository> {
                        on {
                            getTeacher(teacherId = 1)
                        } doReturn Teacher(name = "teacher2", isCreated = false, githubUsername = "test1234", githubId = 123452, token = "token1", id = 2, email = "test1@alunos.isel.pt")
                    }

                    val mockedDeliveriesRepository = mock<DeliveryRepository> {
                        on { getDeliveriesByAssignment(assignmentId = 1) } doReturn listOf()
                        on { getDeliveriesByAssignment(assignmentId = 4) } doReturn listOf(Delivery(id = 1, assignmentId = 1, dueDate = Timestamp.from(Instant.now()), tagControl = "tagControl"))
                    }

                    val mockedTeamsRepository = mock<TeamRepository> {
                        on { getTeamsFromAssignment(assignmentId = 1) } doReturn listOf()
                        on { getTeamsFromStudent(studentId = 1) } doReturn listOf()
                    }
                    on { classroomRepository } doReturn mockedClassroomRepository
                    on { assignmentRepository } doReturn mockedAssignmentRepository
                    on { usersRepository } doReturn mockedUsersRepository
                    on { deliveryRepository } doReturn mockedDeliveriesRepository
                    on { teamRepository } doReturn mockedTeamsRepository
                }
                return block(mockedTransaction)
            }
        }
    }

    // TEST: createAssignment

    @Test
    fun `createAssignment should give an InvalidInput because the userId is invalid`() {
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
    fun `createAssignment should give an NotTeacher because user id is not in database`() {
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
                maxNumberElems = 2,
                dueDate = Timestamp.from(Instant.now()),
            ),
        )

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.NotTeacher)
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
    fun `getAssigmentInfo should give an InvalidInput because assignment id is invalid`() {
        // given: a invalid assignment id
        val assignmentId = -1

        // when: getting an error because the assignment id is invalid
        val assignment = assignmentServices.getTeacherAssignmentInfo(assignmentId = assignmentId)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getAssigmentInfo should give an AssignmentNotFound because assignment id is not in database`() {
        // given: a valid assignment id
        val assignmentId = 5

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

    // TEST: deleteAssignment

    @Test
    fun `deleteAssignment should give an InvalidInput because assignment id is invalid`() {
        // given: a invalid assignment id
        val assignmentId = -1

        // when: getting an error because the assignment id is invalid
        val assignment = assignmentServices.deleteAssignment(assignmentId = assignmentId)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `deleteAssignment should give an AssignmentNotFound because assignment id is not in database`() {
        // given: a valid assignment id
        val assignmentId = 5

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
    fun `getAssignmentStudentTeams should give an InvalidInput because assignment id is invalid`() {
        // given: a invalid assignment id
        val assignmentId = -1

        // when: getting an error because the assignment id is invalid
        val assignment = assignmentServices.getAssignmentStudentTeams(assignmentId = assignmentId, studentId = 1)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getAssignmentStudentTeams should give an InvalidInput because student id is invalid`() {
        // given: a invalid student id
        val studentId = -1

        // when: getting an error because the student id is invalid
        val assignment = assignmentServices.getAssignmentStudentTeams(assignmentId = 1, studentId = studentId)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getAssignmentStudentTeams should give an AssignmentNotFound because assignmed id is not in database`() {
        // given: a valid assignment id
        val assignmentId = 5

        // when: getting a list of teams
        val assignment = assignmentServices.getAssignmentStudentTeams(assignmentId = assignmentId, studentId = 1)

        if (assignment is Result.Problem) {
            assert(assignment.value is AssignmentServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getAssignmentStudentTeams should give a list teams`() {
        // when: getting an error because the assignment id is not in database
        val assignment = assignmentServices.getAssignmentStudentTeams(assignmentId = 1, studentId = 1)

        if (assignment is Result.Success) {
            assert(assignment.value.isEmpty())
        } else {
            fail("Should not be Either.Left")
        }
    }
}
