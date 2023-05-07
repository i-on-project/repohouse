package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.Apply
import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.http.model.input.TeachersPendingInputModel
import com.isel.leic.ps.ion_classcode.repository.ApplyRepository
import com.isel.leic.ps.ion_classcode.repository.CourseRepository
import com.isel.leic.ps.ion_classcode.repository.UsersRepository
import com.isel.leic.ps.ion_classcode.repository.request.RequestRepository
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
                        } doReturn listOf(Course(id = 1, orgUrl = "orgUrl", name = "courseName", teachers = listOf(), isArchived = false, orgId = 1L))
                    }
                    val mockedUserRepository = mock<UsersRepository> {
                        on { getUserById(userId = 1) } doReturn Teacher(name = "test14", id = 1, email = "test@alunos.isel.pt", githubUsername = "test123", githubId = 123, token = "token", isCreated = false)
                        on { getUserById(userId = 2) } doReturn Student(name = "test142", id = 2, email = "test1@alunos.isel.pt", githubUsername = "test124", githubId = 1235, token = "token1", isCreated = false, schoolId = 1234)
                        on { getTeacherGithubToken(id = 1) } doReturn "githubToken"
                    }
                    val mockedApplyRequestRepository = mock<ApplyRepository> {
                        on { getApplyRequests() } doReturn listOf(Apply(id = 1, pendingTeacherId = 1, state = "Pending"), Apply(id = 2, pendingTeacherId = 2, state = "Pending"))
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

        if (teachers is Result.Success) {
            assert(teachers.value.size == 2)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: approveTeachers

    @Test
    fun `approveTeachers should give an InvalidData because the approved and rejected lists are empty`() {
        // when: getting an error because of an invalid lists
        val user = teacherServices.approveTeachers(teachers = TeachersPendingInputModel(
                approved = listOf(),
                rejected = listOf(),
        ))

        if (user is Result.Problem) {
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

        if (user is Result.Success) {
            println(user.value)
            assert(user.value.find { it.id == 1 || it.id == 2 } == null)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: getTeacherGithubToken

    @Test
    fun `getTeacherGithubToken should give an InternalError because the teacherId is invalid`() {
        // given: an invalid teacher id
        val teacherId = -1

        // when: getting an error because of an invalid teacher id
        val user = teacherServices.getTeacherGithubToken(teacherId = teacherId)

        if (user is Result.Problem) {
            assert(user.value is TeacherServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getTeacherGithubToken should give an InternalError because the teacherId is not in database`() {
        // given: a valid teacher id
        val teacherId = 3

        // when: getting an error because the teacher id is not in database
        val user = teacherServices.getTeacherGithubToken(teacherId = teacherId)

        if (user is Result.Problem) {
            assert(user.value is TeacherServicesError.InternalError)
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

        if (user is Result.Success) {
            assert(user.value == "githubToken")
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: createTeacher

    @Test
    fun `createPendingTeacher should be InternalError because the teacher name is empty`() {
        // given: an invalid teacher name
        val name = ""

        // when: creating a teacher should give an error because of an invalid teacher name
        val teacher = teacherServices.createPendingTeacher(
            teacher = TeacherInput(
                name = name,
                email = "test@alunos.isel.pt",
                githubUsername = "test123",
                githubToken = "token",
                githubId = 12341,
                token = "token",
            ),
        )

        // the result should be an error
        if (teacher is Result.Problem) {
            assert(teacher.value is TeacherServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should be InternalError because the teacher email is empty`() {
        // given: an invalid teacher email
        val email = ""

        // when: creating a teacher should give an error because of an invalid teacher email
        val teacher = teacherServices.createPendingTeacher(
            teacher = TeacherInput(
                name = "name",
                email = email,
                githubUsername = "test123",
                githubToken = "token",
                githubId = 12341,
                token = "token",
            ),
        )

        // the result should be an error
        if (teacher is Result.Problem) {
            assert(teacher.value is TeacherServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should be InternalError because the teacher githubToken is empty`() {
        // given: an invalid teacher githubToken
        val githubToken = ""

        // when: creating a teacher should give an error because of an invalid teacher githubToken
        val teacher = teacherServices.createPendingTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "username",
                githubToken = githubToken,
                githubId = 12341,
                token = "token",
            ),
        )

        // the result should be an error
        if (teacher is Result.Problem) {
            assert(teacher.value is TeacherServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should be InternalError because the teacher githubUsername is empty`() {
        // given: an invalid teacher githubUsername
        val githubUsername = ""

        // when: creating a teacher should give an error because of an invalid teacher githubUsername
        val teacher = teacherServices.createPendingTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = githubUsername,
                githubToken = "token",
                githubId = 12346,
                token = "token",
            ),
        )

        // the result should be an error
        if (teacher is Result.Problem) {
            assert(teacher.value is TeacherServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should be InternalError because the teacher githubId is invalid`() {
        // given: an invalid teacher githubId
        val githubId = -1L

        // when: creating a teacher should give an error because of an invalid teacher githubId
        val teacher = teacherServices.createPendingTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "username",
                githubToken = "token",
                githubId = githubId,
                token = "token",
            ),
        )

        // the result should be an error
        if (teacher is Result.Problem) {
            assert(teacher.value is TeacherServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should be InternalError because the teacher token is invalid`() {
        // given: an invalid teacher token
        val token = ""

        // when: creating a teacher should give an error because of an invalid teacher token
        val teacher = teacherServices.createPendingTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "username",
                githubToken = "token",
                githubId = 12346,
                token = token,
            ),
        )

        // the result should be an error
        if (teacher is Result.Problem) {
            assert(teacher.value is TeacherServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should give the pending teacher`() {
        // when: creating a teacher should give the pending teacher
        val teacher = teacherServices.createPendingTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "username",
                githubToken = "token1",
                githubId = 12346,
                token = "token1",
            ),
        )

        // the result should be an error
        if (teacher is Result.Success) {
            assert(teacher.value.id == 1)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should be EmailInUse because the teacher email is already in use`() {
        // when: creating a teacher should give an error because of an email already in use
        val teacher = teacherServices.createPendingTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "username",
                githubToken = "token1",
                githubId = 12346,
                token = "token1",
            ),
        )

        // the result should be an error
        if (teacher is Result.Problem) {
            assert(teacher.value is TeacherServicesError.EmailInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should be GithubIdInUse because the teacher github id is already in use`() {
        // when: creating a teacher should give an error because of GitHub id already in use
        val teacher = teacherServices.createPendingTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test123@alunos.isel.pt",
                githubUsername = "username",
                githubToken = "token1",
                githubId = 12345,
                token = "token1",
            ),
        )

        // the result should be an error
        if (teacher is Result.Problem) {
            assert(teacher.value is TeacherServicesError.GithubIdInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should be GithubUserNameInUse because the teacher github username is already in use`() {
        // when: creating a teacher should give an error because of GitHub username already in use
        val teacher = teacherServices.createPendingTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test123@alunos.isel.pt",
                githubUsername = "test123",
                githubToken = "token1",
                githubId = 5555,
                token = "token1",
            ),
        )

        // the result should be an error
        if (teacher is Result.Problem) {
            assert(teacher.value is TeacherServicesError.GithubUserNameInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should be TokenInUse because the token is already in use`() {
        // when: creating a teacher should give an error because of token already in use
        val teacher = teacherServices.createPendingTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "username",
                githubToken = "token1",
                githubId = 12346,
                token = "token1",
            ),
        )

        // the result should be an error
        if (teacher is Result.Problem) {
            assert(teacher.value is TeacherServicesError.TokenInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should be GithubTokenInUse because the token is already in use`() {
        // when: creating a teacher should give an error because of token already in use
        val teacher = teacherServices.createPendingTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "username",
                githubToken = "token1",
                githubId = 12346,
                token = "ere343",
            ),
        )

        // the result should be an error
        if (teacher is Result.Problem) {
            assert(teacher.value is TeacherServicesError.TokenInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should return the new teacher`() {
        // when: creating a teacher should give a teacher
        val teacherRes = teacherServices.createTeacher(githubId = 12346)

        // the result should be a teacher
        if (teacherRes is Result.Success) {
            assert(teacherRes.value.id == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }

    @Test
    fun `createTeacher should return teacher not found because github id is not valid`() {
        // when: creating a teacher should give a teacher
        val teacherRes = teacherServices.createTeacher(githubId = 6663464)

        // the result should be a teacher
        if (teacherRes is Result.Problem) {
            assert(teacherRes.value is TeacherServicesError.TeacherNotFound)
        } else {
            fail("Should not be Either.Left")
        }
    }
}
