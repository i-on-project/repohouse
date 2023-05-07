package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.repository.CourseRepository
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

@SpringBootTest
class StudentServicesTests {

    @Autowired
    lateinit var studentServices: StudentServices

    @TestConfiguration
    class Config {
        @Bean
        fun transactionManager(): TransactionManager = object : TransactionManager {
            override fun <R> run(block: (Transaction) -> R): R {
                val mockedTransaction = mock<Transaction> {
                    val mockedUserRepository = mock<UsersRepository> {
                        on { getStudentSchoolId(id = 1) } doReturn 123
                        on { getStudentSchoolId(id = 2) } doReturn null
                        on { updateStudentSchoolId(userId = 1, schoolId = 123) } doAnswer {}
                    }

                    val mockedCourseRepository = mock<CourseRepository> {
                        on { getAllUserCourses(userId = 1) } doReturn listOf(Course(id = 1, orgUrl = "orgUrl", name = "name", teachers = listOf(), orgId = 1L))
                    }
                    on { usersRepository } doReturn mockedUserRepository
                    on { courseRepository } doReturn mockedCourseRepository
                }
                return block(mockedTransaction)
            }
        }
    }

    // TEST: getStudentSchoolId

    @Test
    fun `getStudentSchoolId should give an InternalError because the studentId is invalid`() {
        // given: an invalid student id
        val studentId = -1

        // when: getting an error because of an invalid student id
        val student = studentServices.getStudentSchoolId(studentId = studentId)

        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getStudentSchoolId should give an InternalError because the studentId is not in database`() {
        // given: a valid student id
        val studentId = 2

        // when: getting the school id
        val courses = studentServices.getStudentSchoolId(studentId = studentId)

        if (courses is Result.Problem) {
            assert(courses.value is StudentServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getStudentSchoolId should give the school id`() {
        // given: a valid student id
        val studentId = 1

        // when: getting the school id
        val courses = studentServices.getStudentSchoolId(studentId = studentId)

        if (courses is Result.Success) {
            assert(courses.value == 123)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: createStudent

    @Test
    fun `createPendingStudent should be InternalError because the student name is empty`() {
        // given: an invalid student name
        val name = ""

        // when: creating a student should give an error because of an invalid student name
        val student = studentServices.createPendingStudent(
            student = StudentInput(
                name = name,
                email = "test@alunos.isel.pt",
                githubUsername = "test123",
                githubId = 12341,
                token = "token",
            ),
        )

        // the result should be an error
        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingStudent should be InternalError because the student email is empty`() {
        // given: an invalid student email
        val email = ""

        // when: creating a student should give an error because of an invalid student email
        val student = studentServices.createPendingStudent(
            student = StudentInput(
                name = "name",
                email = email,
                githubUsername = "test123",
                githubId = 12341,
                token = "token",
            ),
        )

        // the result should be an error
        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingStudent should be InternalError because the student schoolId is invalid`() {
        // given: an invalid student schoolId
        val schoolId = -1

        // when: creating a student should give an error because of an invalid student schoolId
        val student = studentServices.createPendingStudent(
            student = StudentInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "username",
                schoolId = schoolId,
                githubId = 12341,
                token = "token",
            ),
        )

        // the result should be an error
        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingStudent should be InternalError because the student githubUsername is empty`() {
        // given: an invalid student githubUsername
        val githubUsername = ""

        // when: creating a student should give an error because of an invalid student githubUsername
        val student = studentServices.createPendingStudent(
            student = StudentInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = githubUsername,
                githubId = 12341,
                token = "token",
            ),
        )

        // the result should be an error
        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingStudent should be InternalError because the student githubId is invalid`() {
        // given: an invalid student githubId
        val githubId = -1L

        // when: creating a student should give an error because of an invalid student githubId
        val student = studentServices.createPendingStudent(
            student = StudentInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "username",
                githubId = githubId,
                token = "token",
            ),
        )

        // the result should be an error
        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingStudent should be InternalError because the student token is invalid`() {
        // given: an invalid student token
        val token = ""

        // when: creating a student should give an error because of an invalid student token
        val student = studentServices.createPendingStudent(
            student = StudentInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "username",
                githubId = 12345,
                token = token,
            ),
        )

        // the result should be an error
        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingStudent should give pending student`() {
        // when: creating a student should give the student pending
        val student = studentServices.createPendingStudent(
            student = StudentInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "username",
                githubId = 12345,
                token = "token1",
            ),
        )

        // the result should be an error
        if (student is Result.Success) {
            assert(student.value.id == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }

    @Test
    fun `createPendingStudent should be EmailInUse because the student email is already in use`() {
        // when: creating a student should give an error because of an email already in use
        val student = studentServices.createPendingStudent(
            student = StudentInput(
                name = "username",
                email = "test123@alunos.isel.pt",
                githubUsername = "username",
                githubId = 55555,
                token = "token123",
            ),
        )

        // the result should be an error
        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.EmailInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingStudent should be GithubIdInUse because the student github id is already in use`() {
        // when: creating a student should give an error because of GitHub id already in use
        val student = studentServices.createPendingStudent(
            student = StudentInput(
                name = "name",
                email = "test123@alunos.isel.pt",
                githubUsername = "username",
                githubId = 12345,
                token = "token1",
            ),
        )

        // the result should be an error
        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.GithubIdInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingStudent should be GithubUserNameInUse because the student github username is already in use`() {
        // when: creating a student should give an error because of GitHub username already in use
        val student = studentServices.createPendingStudent(
            student = StudentInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "test123",
                githubId = 12345,
                token = "token1",
            ),
        )

        // the result should be an error
        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.GithubUserNameInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingStudent should be TokenInUse because the student token is already in use`() {
        // when: creating a student should give an error because of token already in use
        val student = studentServices.createPendingStudent(
            student = StudentInput(
                name = "name",
                email = "test123@alunos.isel.pt",
                githubUsername = "username",
                githubId = 12345,
                token = "token1",
            ),
        )

        // the result should be an error
        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.TokenInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }



    // Create Student

    @Test
    fun `createStudent should be StudentNotFound because the student githubId in invalid`() {
        // given: a student githubId
        val githubId = -1L

        // when: creating a student should give an error because the student githubId in invalid
        val student = studentServices.createStudent(
            githubId = githubId,
            schoolId = 222,
        )

        // the result should be an error
        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.StudentNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be InvalidData because the student schoolId in invalid`() {
        // given: a student schoolId
        val schoolId = -1

        // when: creating a student should give an error because the student schoolId in invalid
        val student = studentServices.createStudent(
            githubId = 123,
            schoolId = schoolId,
        )

        // the result should be an error
        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be SchoolIdInUse because the student school id is already in use`() {
        // given: a student school id and github id
        val githubId = 12345L
        val schoolId = 1235
        // when: creating a student should give an error because of school id already in use
        val student = studentServices.createStudent(
            githubId = githubId,
            schoolId = schoolId,
        )

        // the result should be an error
        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.SchoolIdInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should return the new student`() {
        // when: creating a student should give a student
        val studentRes = studentServices.createStudent(githubId = 12345, schoolId = 1238)

        // the result should be a student
        if (studentRes is Result.Success) {
            assert(studentRes.value.id == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }
}
