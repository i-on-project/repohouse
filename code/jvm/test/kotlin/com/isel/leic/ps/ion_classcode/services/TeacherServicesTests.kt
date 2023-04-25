package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.requests.Apply
import com.isel.leic.ps.ion_classcode.http.model.input.TeachersPendingInputModel
import com.isel.leic.ps.ion_classcode.http.services.TeacherServices
import com.isel.leic.ps.ion_classcode.http.services.TeacherServicesError
import com.isel.leic.ps.ion_classcode.repository.CourseRepository
import com.isel.leic.ps.ion_classcode.repository.UsersRepository
import com.isel.leic.ps.ion_classcode.repository.request.ApplyRequestRepository
import com.isel.leic.ps.ion_classcode.repository.request.RequestRepository
import com.isel.leic.ps.ion_classcode.repository.transaction.Transaction
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@SpringBootTest
class TeacherServicesTests {

    @Autowired
    lateinit var teacherServices: TeacherServices

    @TestConfiguration
    class Config {
        @Bean
        fun transactionManager(): TransactionManager = object : TransactionManager {
            override fun <R> run(block: (Transaction) -> R): R {
                val mockedTransaction = mock<Transaction> {
                    val mockedCourseRepository = mock<CourseRepository> {
                        on {
                            getAllUserCourses(1)
                        } doReturn listOf(Course(id = 1, orgUrl = "orgUrl", name = "courseName", teachers = listOf<Teacher>(), isArchived = false))
                    }
                    val mockedUserRepository = mock<UsersRepository> {
                        on { getUserById(userId = 1) } doReturn Teacher(name = "test14", id = 1, email = "test@alunos.isel.pt", githubUsername = "test123", githubId = 123, token = "token", isCreated = false)
                        on { getUserById(userId = 2) } doReturn Student(name = "test142", id = 2, email = "test1@alunos.isel.pt", githubUsername = "test124", githubId = 1235, token = "token1", isCreated = false, schoolId = 1234)
                        on { getTeacherGithubToken(id = 1) } doReturn "githubToken"
                    }
                    val mockedApplyRequestRepository = mock<ApplyRequestRepository> {
                        on { getApplyRequests() } doReturn listOf(Apply(id = 1, creator = 1), Apply(id = 2, creator = 2))
                    }
                    val mockedRequestRepository = mock<RequestRepository> {
                        on { changeStateRequest(id = 1, state = "Approved") } doAnswer {}
                        on { changeStateRequest(id = 2, state = "Rejected") } doAnswer {}
                    }
                    on { courseRepository } doReturn mockedCourseRepository
                    on { usersRepository } doReturn mockedUserRepository
                    on { applyRequestRepository } doReturn mockedApplyRequestRepository
                    on { requestRepository } doReturn mockedRequestRepository
                }
                return block(mockedTransaction)
            }
        }
    }

    // TEST: getTeachersNeedingApproval

    @Test
    fun `getTeachersNeedingApproval should give a list of teachers that need approval`() {
        // given: a list of teachers that need approval
        val teachers = teacherServices.getTeachersNeedingApproval()

        if (teachers is Either.Right) {
            assert(teachers.value.size == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: approveTeachers

    @Test
    fun `approveTeachers should give an InvalidData because the approved and rejected lists are empty`() {
        // when: getting an error because of an invalid lists
        val user = teacherServices.approveTeachers(
            teachers = TeachersPendingInputModel(
                approved = listOf<Int>(),
                rejected = listOf<Int>(),
            ),
        )

        if (user is Either.Left) {
            assert(user.value is TeacherServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `approved teachers should be true`() {
        // when: getting a flag  that everything went well
        val user = teacherServices.approveTeachers(
            teachers = TeachersPendingInputModel(
                approved = listOf(1),
                rejected = listOf(2),
            ),
        )

        if (user is Either.Right) {
            println(user.value)
            assert(user.value.find { it.id == 1 || it.id == 2 } == null)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: getTeacherGithubToken

    @Test
    fun `getTeacherGithubToken should give an InvalidData because the teacherId is invalid`() {
        // given: an invalid teacher id
        val teacherId = -1

        // when: getting an error because of an invalid teacher id
        val user = teacherServices.getTeacherGithubToken(teacherId = teacherId)

        if (user is Either.Left) {
            assert(user.value is TeacherServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getTeacherGithubToken should give an TeacherNotFound because the teacherId is not in database`() {
        // given: a valid teacher id
        val teacherId = 3

        // when: getting an error because the teacher id is not in database
        val user = teacherServices.getTeacherGithubToken(teacherId = teacherId)

        if (user is Either.Left) {
            assert(user.value is TeacherServicesError.TeacherNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getTeacherGithubToken should give the github token of a teacher`() {
        // given: a valid teacher id
        val teacherId = 1

        // when: getting the github token of a teacher
        val user = teacherServices.getTeacherGithubToken(teacherId = teacherId)

        if (user is Either.Right) {
            assert(user.value == "githubToken")
        } else {
            fail("Should not be Either.Left")
        }
    }
}
