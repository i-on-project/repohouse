package com.isel.leic.ps.ionClassCode.services

import com.isel.leic.ps.ionClassCode.domain.Course
import com.isel.leic.ps.ionClassCode.domain.PendingStudent
import com.isel.leic.ps.ionClassCode.domain.PendingTeacher
import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.Teacher
import com.isel.leic.ps.ionClassCode.domain.input.StudentInput
import com.isel.leic.ps.ionClassCode.domain.input.TeacherInput
import com.isel.leic.ps.ionClassCode.repository.CourseRepository
import com.isel.leic.ps.ionClassCode.repository.UsersRepository
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

@SpringBootTest
class UserServiceTests {

    @Autowired
    lateinit var userServices: UserServices

    @TestConfiguration
    class Config {
        @Bean
        fun transactionManager(): TransactionManager = object : TransactionManager {
            override fun <R> run(block: (Transaction) -> R): R {
                val mockedTransaction = mock<Transaction> {
                    val mockedUsersRepository = mock<UsersRepository> {
                        on {
                            createPendingStudent(
                                student = StudentInput(
                                    name = "name",
                                    email = "test@alunos.isel.pt",
                                    githubUsername = "username",
                                    githubId = 12345,
                                    token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=",
                                    schoolId = null,
                                ),
                            )
                        } doReturn PendingStudent(
                            name = "name",
                            email = "test@alunos.isel.pt",
                            githubUsername = "username",
                            token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=",
                            githubId = 12345,
                            isCreated = false,
                            id = 1,
                        )

                        on {
                            createStudent(
                                student = StudentInput(
                                    email = "test@alunos.isel.pt",
                                    githubUsername = "username",
                                    schoolId = 1238,
                                    githubId = 12345,
                                    token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=",
                                    name = "name",
                                ),
                            )
                        } doReturn Student(
                            email = "test@alunos.isel.pt",
                            githubUsername = "username",
                            schoolId = 1238,
                            githubId = 1240,
                            token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=",
                            name = "student",
                            id = 1,
                            isCreated = true,
                        )

                        on {
                            storeChallengeInfo(challenge = "challenge", challengeMethod = "s256", state = "state")
                        } doAnswer {}

                        on {
                            updateStudentSchoolId(userId = 5, schoolId = 1256)
                        } doAnswer {}

                        on {
                            createPendingTeacher(
                                teacher = TeacherInput(
                                    name = "name",
                                    email = "test@alunos.isel.pt",
                                    githubUsername = "username",
                                    githubToken = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=",
                                    githubId = 12346,
                                    token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=",
                                ),
                            )
                        } doReturn PendingTeacher(
                            name = "name",
                            id = 1,
                            email = "test@alunos.isel.pt",
                            githubUsername = "username",
                            githubId = 12346,
                            token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=",
                            isCreated = false,
                            githubToken = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=",
                        )

                        on {
                            createTeacher(
                                teacher = TeacherInput(
                                    email = "test@alunos.isel.pt",
                                    name = "name",
                                    token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=",
                                    githubId = 12346,
                                    githubUsername = "username",
                                    githubToken = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=",
                                ),
                            )
                        } doReturn Teacher(
                            email = "test@alunos.isel.pt",
                            name = "name",
                            token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=",
                            githubId = 12346,
                            githubUsername = "username",
                            id = 1,
                            isCreated = true,
                        )

                        on {
                            getStudent(studentId = 4)
                        } doReturn Student(
                            name = "student2",
                            token = "token3",
                            githubId = 1234152,
                            githubUsername = "test12345",
                            isCreated = false,
                            email = "test3@alunos.isel.pt",
                            id = 4,
                            schoolId = 1235,
                        )

                        on {
                            getTeacher(teacherId = 2)
                        } doReturn Teacher(
                            name = "teacher2",
                            isCreated = false,
                            githubUsername = "test1234",
                            githubId = 123452,
                            token = "token1",
                            id = 2,
                            email = "test1@alunos.isel.pt",
                        )

                        on {
                            getUserById(userId = 4)
                        } doReturn Student(
                            name = "student2",
                            token = "token3",
                            githubId = 1234152,
                            githubUsername = "test12345",
                            isCreated = false,
                            email = "test3@alunos.isel.pt",
                            id = 4,
                            schoolId = 1235,
                        )

                        on {
                            getUserById(userId = 1)
                        } doReturn Student(
                            name = "student2",
                            token = "token3",
                            githubId = 1234152,
                            githubUsername = "test12345",
                            isCreated = false,
                            email = "test3@alunos.isel.pt",
                            id = 1,
                            schoolId = 1235,
                        )

                        on {
                            verifySecret(secret = "secret", state = "state")
                        } doReturn true

                        on {
                            getUserByGithubId(githubId = 12555L)
                        } doReturn Teacher(
                            name = "teacher2",
                            isCreated = false,
                            githubUsername = "test1234",
                            githubId = 1234,
                            token = "token1",
                            id = 2,
                            email = "test1@alunos.isel.pt",
                        )

                        on {
                            getUserByToken(token = "bearer")
                        } doReturn Teacher(
                            name = "teacher2",
                            isCreated = false,
                            githubUsername = "test1234",
                            githubId = 1234,
                            token = "token1",
                            id = 2,
                            email = "test1@alunos.isel.pt",
                        )

                        on {
                            checkIfEmailExists(email = "test5@alunos.isel.pt")
                        } doReturn true

                        on {
                            checkIfGithubIdExists(githubId = 1233)
                        } doReturn true

                        on {
                            checkIfGithubIdExists(githubId = 1237)
                        } doReturn true

                        on {
                            checkIfGithubTokenExists(githubToken = "PEaenWxYddN6Q_NT1PiOYfz4EsZu7jRXRlpAsNpBU-A=")
                        } doReturn true

                        on {
                            checkIfGithubUsernameExists(githubUsername = "test142")
                        } doReturn true

                        on {
                            checkIfTokenExists(token = "PEaenWxYddN6Q_NT1PiOYfz4EsZu7jRXRlpAsNpBU-A=")
                        } doReturn true

                        on {
                            checkIfSchoolIdExists(schoolId = 1235)
                        } doReturn true
                    }
                    val mockedCourseRepository = mock<CourseRepository> {
                        on { getAllStudentCourses(userId = 1) } doReturn listOf(Course(id = 1, orgUrl = "orgUrl", name = "name", teachers = listOf(), orgId = 1L))
                    }
                    on { usersRepository } doReturn mockedUsersRepository
                    on { courseRepository } doReturn mockedCourseRepository
                }
                return block(mockedTransaction)
            }
        }
    }

    // TEST: getUserByGithubId

    @Test
    fun `getUserByGithubId should give an InvalidGithubId because the githubId is invalid`() {
        // given: an invalid github id
        val githubId = -1L

        // when: getting an error because of an invalid github id
        val user = userServices.getUserByGithubId(githubId = githubId)

        if (user is Result.Problem) {
            assert(user.value is UserServicesError.UserNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getUserByGithubId should give an null because the user doesn't exists`() {
        // given: a valid github id that is not in the database
        val githubId = 5L

        // when: getting a null because the github is not in the database
        val user = userServices.getUserByGithubId(githubId = githubId)

        // the result should be an error
        if (user is Result.Problem) {
            assert(user.value is UserServicesError.UserNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getUserByGithubId should give an user`() {
        val name = "teacher2"
        // given: a valid github id that is in the database
        val githubId = 12555L

        // when: getting an error because of an invalid github id
        val user = userServices.getUserByGithubId(githubId = githubId)

        // the result should be a user
        if (user is Result.Success) {
            assert(user.value.name == name)
        } else {
            fail("User not found")
        }
    }

    // TEST: checkAuthentication

    @Test
    fun `checkAuthentication should give an InvalidToken because the token is invalid`() {
        // given: an invalid bearer token
        val bearerToken = ""

        // when: getting an error because of an invalid github id
        val user = userServices.checkAuthentication(token = bearerToken)

        if (user is Result.Problem) {
            assert(user.value is UserServicesError.InvalidToken)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `checkAuthentication should give an null because the user doesn't exists`() {
        // given: a valid bearer token that is not in the database
        val bearerToken = "notInDatabase"

        // when: getting a null because the bearer token is not in the database
        val user = userServices.checkAuthentication(token = bearerToken)

        // the result should be an error
        if (user is Result.Problem) {
            assert(user.value is UserServicesError.UserNotAuthenticated)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `checkAuthentication should give an user`() {
        val name = "teacher2"
        // given: a valid token that is in the database
        val bearerToken = "bearer"

        // when: getting a user
        val user = userServices.checkAuthentication(token = bearerToken)

        // the result should be a user
        if (user is Result.Success) {
            assert(user.value.name == name)
        } else {
            fail("User not found")
        }
    }

    // TEST: getCourses

    @Test
    fun `getCourses should give an InternalError because the studentId is invalid`() {
        // given: an invalid student id
        val studentId = -1

        // when: getting an error because of an invalid student id
        val courses = userServices.getAllUserCourses(userId = studentId)

        if (courses is Result.Problem) {
            assert(courses.value is UserServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getCourses should give a list of courses`() {
        // given: a valid student id
        val studentId = 1

        // when: getting a list of courses
        val courses = userServices.getAllUserCourses(userId = studentId)

        if (courses is Result.Success) {
            assert(courses.value.size == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: getCourses

    @Test
    fun `getCourses should give an InvalidError because the teacherId is invalid`() {
        // given: an invalid teacher id
        val teacherId = -1

        // when: getting an error because of an invalid teacher id
        val user = userServices.getAllUserCourses(userId = teacherId)

        if (user is Result.Problem) {
            assert(user.value is UserServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getCourses should give the list of courses of a teacher`() {
        // given: a valid teacher id
        val teacherId = 1

        // when: getting a list of courses
        val user = userServices.getAllUserCourses(userId = teacherId)

        if (user is Result.Success) {
            assert(user.value.size == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: verifySecret

    @Test
    fun `verifySecret should give an InvalidData because the secret is invalid`() {
        // given: an invalid secret
        val secret = ""

        // when: getting an error because of an invalid secret
        val result = userServices.verifySecret(secret = secret, state = "state")

        if (result is Result.Problem) {
            assert(result.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `verifySecret should give an InvalidData because the state is invalid`() {
        // given: an invalid state
        val state = ""

        // when: getting an error because of an invalid state
        val result = userServices.verifySecret(secret = "secret", state = state)

        if (result is Result.Problem) {
            assert(result.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `verifySecret should give an true because the secret is valid`() {
        // when: getting true
        val result = userServices.verifySecret(secret = "secret", state = "state")

        if (result is Result.Success) {
            assert(result.value == Unit)
        } else {
            fail("Should not be Either.Right")
        }
    }

    // storeAuthInfo

    @Test
    fun `storeAuthInfo should give an InvalidData because the challengeMethod is invalid`() {
        // given: an invalid challengeMethod
        val challengeMethod = ""

        // when: getting an error because of an invalid challengeMethod
        val result = userServices.storeChallengeInfo(challengeMethod = challengeMethod, state = "state", challenge = "challenge")

        if (result is Result.Problem) {
            assert(result.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `storeAuthInfo should give an InvalidData because the state is invalid`() {
        // given: an invalid state
        val state = ""

        // when: getting an error because of an invalid state
        val result = userServices.storeChallengeInfo(challengeMethod = "challengeMethod", state = state, challenge = "challenge")

        if (result is Result.Problem) {
            assert(result.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `storeAuthInfo should give an InvalidData because the challenge is invalid`() {
        // given: an invalid challenge
        val challenge = ""

        // when: getting an error because of an invalid challenge
        val result = userServices.storeChallengeInfo(challengeMethod = "challengeMethod", state = "state", challenge = challenge)

        if (result is Result.Problem) {
            assert(result.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `storeAuthInfo should give an InvalidData because the challengeMethod have invalid format`() {
        // given: an invalid challengeMethod
        val challengeMethod = "heelo"

        // when: getting an error because of an invalid challengeMethod
        val result = userServices.storeChallengeInfo(challengeMethod = challengeMethod, state = "state", challenge = "challenge")

        if (result is Result.Problem) {
            assert(result.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `storeAuthInfo should be able to store the info`() {
        // when: getting unit
        val result = userServices.storeChallengeInfo(challengeMethod = "s256", state = "state", challenge = "challenge")

        if (result is Result.Success) {
            assert(result.value == Unit)
        } else {
            fail("Should not be Either.Right")
        }
    }
}
