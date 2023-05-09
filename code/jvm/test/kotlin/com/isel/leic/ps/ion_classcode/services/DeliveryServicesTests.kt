package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.Assignment
import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.Delivery
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.Team
import com.isel.leic.ps.ion_classcode.domain.input.DeliveryInput
import com.isel.leic.ps.ion_classcode.repository.AssignmentRepository
import com.isel.leic.ps.ion_classcode.repository.ClassroomRepository
import com.isel.leic.ps.ion_classcode.repository.DeliveryRepository
import com.isel.leic.ps.ion_classcode.repository.UsersRepository
import com.isel.leic.ps.ion_classcode.repository.transaction.Transaction
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Result
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
class DeliveryServicesTests {

    companion object {
        val time: Timestamp = Timestamp.from(Instant.now())
    }

    @Autowired
    lateinit var deliveryServices: DeliveryServices

    @TestConfiguration
    class Config {
        @Bean
        fun transactionManager(): TransactionManager = object : TransactionManager {
            override fun <R> run(block: (Transaction) -> R): R {
                val mockedTransaction = mock<Transaction> {
                    val mockedDeliveryRepository = mock<DeliveryRepository> {
                        on {
                            createDelivery(
                                delivery = DeliveryInput(
                                    dueDate = time,
                                    assignmentId = 2,
                                    tagControl = "tagControl",
                                ),
                            )
                        } doReturn Delivery(
                            id = 2,
                            dueDate = time,
                            assignmentId = 2,
                            tagControl = "tagControl2",
                        )
                        on { getDeliveryById(deliveryId = 1) } doReturn Delivery(
                            id = 1,
                            dueDate = time,
                            assignmentId = 1,
                            tagControl = "tagControl",
                        )
                        on { getDeliveryById(deliveryId = 2) } doReturn Delivery(
                            id = 2,
                            dueDate = time,
                            assignmentId = 2,
                            tagControl = "tagControl2",
                        )
                        on { getDeliveryById(deliveryId = 3) } doReturn Delivery(
                            id = 3,
                            dueDate = time,
                            assignmentId = 3,
                            tagControl = "tagControl3",
                        )
                        on { getDeliveryById(deliveryId = 4) } doReturn Delivery(
                            id = 4,
                            dueDate = time,
                            assignmentId = 4,
                            tagControl = "tagControl4",
                        )
                        on { getDeliveryById(deliveryId = 5) } doReturn Delivery(
                            id = 5,
                            dueDate = time,
                            assignmentId = 2,
                            tagControl = "tagControl5",
                        )
                        on { getTeamsByDelivery(deliveryId = 2) } doReturn listOf(
                            Team(
                                id = 2,
                                name = "Team1",
                                isCreated = false,
                                assignment = 1,
                            ),
                        )
                        on { getTeamsByDelivery(deliveryId = 5) } doReturn listOf()
                    }
                    val mockedAssignmentRepository = mock<AssignmentRepository> {
                        on { getAssignmentById(assignmentId = 1) } doReturn Assignment(
                            id = 1,
                            classroomId = 1,
                            maxElemsPerGroup = 2,
                            maxNumberGroups = 2,
                            releaseDate = Timestamp.from(Instant.now()),
                            description = "description",
                            title = "title",
                        )
                        on { getAssignmentById(assignmentId = 2) } doReturn Assignment(
                            id = 2,
                            classroomId = 2,
                            maxElemsPerGroup = 2,
                            maxNumberGroups = 2,
                            releaseDate = Timestamp.from(Instant.now()),
                            description = "description2",
                            title = "title2",
                        )
                        on { getAssignmentById(assignmentId = 3) } doReturn Assignment(
                            id = 3,
                            classroomId = 3,
                            maxElemsPerGroup = 2,
                            maxNumberGroups = 2,
                            releaseDate = Timestamp.from(Instant.now()),
                            description = "description3",
                            title = "title3",
                        )
                    }
                    val mockedClassroomRepository = mock<ClassroomRepository> {
                        on { getClassroomById(classroomId = 1) } doReturn Classroom(
                            id = 1,
                            name = "Classroom 1",
                            inviteLink = "inviteLink",
                            isArchived = true,
                            lastSync = Timestamp.from(Instant.now()),
                            courseId = 1,
                        )
                        on { getClassroomById(classroomId = 2) } doReturn Classroom(
                            id = 2,
                            name = "Classroom 2",
                            inviteLink = "inviteLink2",
                            isArchived = false,
                            lastSync = Timestamp.from(Instant.now()),
                            courseId = 1,
                        )
                    }
                    val mockedUsersRepository = mock<UsersRepository> {
                        on { getTeacher(teacherId = 1) } doReturn Teacher(
                            name = "teacher2",
                            isCreated = false,
                            githubUsername = "test1234",
                            githubId = 123452,
                            token = "token1",
                            id = 2,
                            email = "test1@alunos.isel.pt",
                        )
                    }
                    on { assignmentRepository } doReturn mockedAssignmentRepository
                    on { classroomRepository } doReturn mockedClassroomRepository
                    on { usersRepository } doReturn mockedUsersRepository
                    on { deliveryRepository } doReturn mockedDeliveryRepository
                }
                return block(mockedTransaction)
            }
        }
    }

    // TEST: createDelivery

    @Test
    fun `createDelivery should give an InternalError because the userId is invalid`() {
        // given: an invalid user id
        val userId = -1

        // when: getting an error because of an invalid user id
        val delivery = deliveryServices.createDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = Timestamp.from(Instant.now()),
                assignmentId = 1,
                tagControl = "tagControl",
            ),
            userId = userId,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createDelivery should give an AssignmentNotFound because the assignmentId is invalid`() {
        // given: an invalid assignment id
        val assignmentId = -1

        // when: getting an error because of an invalid assignment id
        val delivery = deliveryServices.createDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = Timestamp.from(Instant.now()),
                assignmentId = assignmentId,
                tagControl = "tagControl",
            ),
            userId = 1,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createDelivery should give an InvalidInput because the tagControl is invalid`() {
        // given: an invalid tag control
        val tagControl = ""

        // when: getting an error because of an invalid tag control
        val delivery = deliveryServices.createDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = Timestamp.from(Instant.now()),
                assignmentId = 1,
                tagControl = tagControl,
            ),
            userId = 1,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createDelivery should give an AssignmentNotFound because the assignmentId is not in database`() {
        // given: an valid assignment id
        val assignmentId = 4

        // when: getting an error because the assignment id is not in database
        val delivery = deliveryServices.createDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = Timestamp.from(Instant.now()),
                assignmentId = assignmentId,
                tagControl = "tagControl",
            ),
            userId = 1,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createDelivery should give an ClassroomNotFound because the classroom is not in database`() {
        // given: an valid assignment id
        val assignmentId = 3

        // when: getting an error because the classroom is not in database
        val delivery = deliveryServices.createDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = Timestamp.from(Instant.now()),
                assignmentId = assignmentId,
                tagControl = "tagControl",
            ),
            userId = 1,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createDelivery should give an InternalError because the is not a teacher`() {
        // given: an valid user id
        val userId = 2

        // when: getting an error because the user is not a teacher
        val delivery = deliveryServices.createDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = Timestamp.from(Instant.now()),
                assignmentId = 2,
                tagControl = "tagControl",
            ),
            userId = userId,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createDelivery should give the id of the delivery`() {
        // when: getting the id of the delivery
        val delivery = deliveryServices.createDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = time,
                assignmentId = 2,
                tagControl = "tagControl",
            ),
            userId = 1,
        )

        if (delivery is Result.Success) {
            assert(delivery.value.id == 2)
        } else {
            fail("Should not be Either.Right")
        }
    }

    // TEST: getDeliveryInfo

    @Test
    fun `getDeliveryInfo should give an DeliveryNotFound because the deliveryId is invalid`() {
        // given: an invalid delivery id
        val deliveryId = -1

        // when: getting an error because of an invalid delivery id
        val delivery = deliveryServices.getDeliveryInfo(
            deliveryId = deliveryId,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.DeliveryNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getDeliveryInfo should give an DeliveryNotFound because the delivery is not in database`() {
        // given: an valid delivery id
        val deliveryId = 6

        // when: getting an error because the delivery is not in database
        val delivery = deliveryServices.getDeliveryInfo(
            deliveryId = deliveryId,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.DeliveryNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getDeliveryInfo should give the delivery info`() {
        // when: getting the delivery info
        val delivery = deliveryServices.getDeliveryInfo(
            deliveryId = 1,
        )

        if (delivery is Result.Success) {
            assert(delivery.value.delivery.tagControl == "tagControl")
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: deleteDelivery

    @Test
    fun `deleteDelivery should give an DeliveryNotFound because the deliveryId is invalid`() {
        // given: an invalid delivery id
        val deliveryId = -1

        // when: getting an error because of an invalid delivery id
        val delivery = deliveryServices.deleteDelivery(
            deliveryId = deliveryId,
            userId = 1,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.DeliveryNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `deleteDelivery should give an InternalError because the userId is invalid`() {
        // given: an invalid user id
        val userId = -1

        // when: getting an error because of an invalid user id
        val delivery = deliveryServices.deleteDelivery(
            deliveryId = 1,
            userId = userId,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `deleteDelivery should give an InternalError because the is not a teacher`() {
        // given: an valid user id
        val userId = 3

        // when: getting an error because the user is not a teacher
        val delivery = deliveryServices.deleteDelivery(
            deliveryId = 1,
            userId = userId,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `deleteDelivery should give an AssignmentNotFound because the assignmentId is not in database`() {
        // given: an valid delivery id
        val deliveryId = 4

        // when: getting an error because the assignment id is not in database
        val delivery = deliveryServices.deleteDelivery(
            deliveryId = deliveryId,
            userId = 1,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `deleteDelivery should give an ClassroomNotFound because the classroom is not in database`() {
        // given: an valid assignment id
        val deliveryId = 3

        // when: getting an error because the classroom is not in database
        val delivery = deliveryServices.deleteDelivery(
            deliveryId = deliveryId,
            userId = 1,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `deleteDelivery should give an ClassroomArchived because the classroom is already archived`() {
        // given: an valid assignment id
        val deliveryId = 1

        // when: getting an error because the classroom is already archived
        val delivery = deliveryServices.deleteDelivery(
            deliveryId = deliveryId,
            userId = 1,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.ClassroomArchived)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `deleteDelivery should give an DeliveryWithTeams because the delivery still have teams associated`() {
        // given: an valid assignment id
        val deliveryId = 5

        // when: getting an error because the delivery still have teams associated
        val delivery = deliveryServices.deleteDelivery(
            deliveryId = deliveryId,
            userId = 1,
        )

        if (delivery is Result.Success) {
            assert(delivery.value)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: updateDelivery

    @Test
    fun `updateDelivery should give an DeliveryNotFound because the deliveryId is invalid`() {
        // given: an invalid delivery id
        val deliveryId = -1

        // when: getting an error because of an invalid delivery id
        val delivery = deliveryServices.updateDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = Timestamp.from(Instant.now()),
                assignmentId = 1,
                tagControl = "tagControl",
            ),
            deliveryId = deliveryId,
            userId = 1,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.DeliveryNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateDelivery should give an InternalError because the userId is invalid`() {
        // given: an invalid user id
        val userId = -1

        // when: getting an error because of an invalid user id
        val delivery = deliveryServices.updateDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = Timestamp.from(Instant.now()),
                assignmentId = 1,
                tagControl = "tagControl",
            ),
            deliveryId = 1,
            userId = userId,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateDelivery should give an AssignmentNotFound because the assignmentId is invalid`() {
        // given: an invalid assignment id
        val assignmentId = -1

        // when: getting an error because of an invalid assignment id
        val delivery = deliveryServices.updateDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = Timestamp.from(Instant.now()),
                assignmentId = assignmentId,
                tagControl = "tagControl",
            ),
            deliveryId = 1,
            userId = 1,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateDelivery should give an InvalidInput because the tagControl is invalid`() {
        // given: an invalid tag control
        val tagControl = ""

        // when: getting an error because of an invalid tag control
        val delivery = deliveryServices.updateDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = Timestamp.from(Instant.now()),
                assignmentId = 1,
                tagControl = tagControl,
            ),
            deliveryId = 1,
            userId = 1,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateDelivery should give an InternalError because the is not a teacher`() {
        // given: an valid user id
        val userId = 4

        // when: getting an error because the user is not a teacher
        val delivery = deliveryServices.updateDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = Timestamp.from(Instant.now()),
                assignmentId = 2,
                tagControl = "tagControl",
            ),
            deliveryId = 2,
            userId = userId,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateDelivery should give an AssignmentNotFound because the assignmentId is not in database`() {
        // given: an valid assignment id
        val assignmentId = 4

        // when: getting an error because the assignment id is not in database
        val delivery = deliveryServices.updateDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = Timestamp.from(Instant.now()),
                assignmentId = assignmentId,
                tagControl = "tagControl",
            ),
            deliveryId = 4,
            userId = 1,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.AssignmentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateDelivery should give an ClassroomNotFound because the classroom is not in database`() {
        // given: an valid assignment id
        val assignmentId = 3

        // when: getting an error because the classroom is not in database
        val delivery = deliveryServices.updateDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = Timestamp.from(Instant.now()),
                assignmentId = assignmentId,
                tagControl = "tagControl",
            ),
            deliveryId = 3,
            userId = 1,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateDelivery should give an ClassroomArchived because the classroom is already archived`() {
        // given: an valid assignment id
        val assignmentId = 1

        // when: getting an error because the classroom is already archived
        val delivery = deliveryServices.updateDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = Timestamp.from(Instant.now()),
                assignmentId = assignmentId,
                tagControl = "tagControl",
            ),
            deliveryId = 1,
            userId = 1,
        )

        if (delivery is Result.Problem) {
            assert(delivery.value is DeliveryServicesError.ClassroomArchived)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateDelivery should give a flag that say that it was a success`() {
        // given: an valid assignment id
        val assignmentId = 2

        // when: getting an error because the classroom is already archived
        val delivery = deliveryServices.updateDelivery(
            deliveryInfo = DeliveryInput(
                dueDate = Timestamp.from(Instant.now()),
                assignmentId = assignmentId,
                tagControl = "tagControl",
            ),
            deliveryId = 2,
            userId = 1,
        )

        if (delivery is Result.Success) {
            assert(delivery.value)
        } else {
            fail("Should not be Either.Left")
        }
    }
}
