package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.PendingStudent
import com.isel.leic.ps.ion_classcode.domain.PendingTeacher
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.http.services.StudentServices
import com.isel.leic.ps.ion_classcode.http.services.StudentServicesError
import com.isel.leic.ps.ion_classcode.http.services.TeacherServices
import com.isel.leic.ps.ion_classcode.http.services.TeacherServicesError
import com.isel.leic.ps.ion_classcode.http.services.UserServices
import com.isel.leic.ps.ion_classcode.http.services.UserServicesError
import com.isel.leic.ps.ion_classcode.repository.UsersRepository
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
class UserServiceTests {

    @Autowired
    lateinit var userServices: UserServices

    @Autowired
    lateinit var studentServices: StudentServices

    @Autowired
    lateinit var teacherServices: TeacherServices

    @TestConfiguration
    class Config {
        @Bean
        fun transactionManager(): TransactionManager = object : TransactionManager {
            override fun <R> run(block: (Transaction) -> R): R {
                val mockedTransaction = mock<Transaction> {
                    val mockedUsersRepository = mock<UsersRepository> {
                        on {
                            createPendingStudent(student = StudentInput(name = "name", email = "test@alunos.isel.pt", githubUsername = "username", githubId = 12345, token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=", schoolId = null))
                        } doReturn PendingStudent(name = "name", email = "test@alunos.isel.pt", githubUsername = "username", token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=", githubId = 12345, isCreated = false, id = 1)

                        on {
                            createStudent(student = StudentInput(email = "test@alunos.isel.pt", githubUsername = "username", schoolId = 1238, githubId = 12345, token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=", name = "name"))
                        } doReturn Student(email = "test@alunos.isel.pt", githubUsername = "username", schoolId = 1238, githubId = 1240, token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=", name = "student", id = 1, isCreated = true)

                        on {
                            updateStudentSchoolId(userId = 5, schoolId = 1256)
                        } doAnswer {}

                        on {
                            createPendingTeacher(teacher = TeacherInput(name = "name", email = "test@alunos.isel.pt", githubUsername = "username", githubToken = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=", githubId = 12346, token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4="))
                        } doReturn PendingTeacher(name = "name", id = 1, email = "test@alunos.isel.pt", githubUsername = "username", githubId = 12346, token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=", isCreated = false, githubToken = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=")

                        on {
                            createTeacher(teacher = TeacherInput(email = "test@alunos.isel.pt", name = "name", token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=", githubId = 12346, githubUsername = "username", githubToken = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4="))
                        } doReturn Teacher(email = "test@alunos.isel.pt", name = "name", token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=", githubId = 12346, githubUsername = "username", id = 1, isCreated = true)

                        on {
                            getStudent(studentId = 4)
                        } doReturn Student(name = "student2", token = "token3", githubId = 1234152, githubUsername = "test12345", isCreated = false, email = "test3@alunos.isel.pt", id = 4, schoolId = 1235)

                        on {
                            getTeacher(teacherId = 2)
                        } doReturn Teacher(name = "teacher2", isCreated = false, githubUsername = "test1234", githubId = 123452, token = "token1", id = 2, email = "test1@alunos.isel.pt")

                        on {
                            getUserById(userId = 4)
                        } doReturn Student(name = "student2", token = "token3", githubId = 1234152, githubUsername = "test12345", isCreated = false, email = "test3@alunos.isel.pt", id = 4, schoolId = 1235)

                        // email
                        on {
                            getPendingUserByGithubId(githubId = 1231)
                        } doReturn PendingTeacher(name = "name", isCreated = false, githubUsername = "username", githubId = 1111, token = "token", id = 1, email = "test5@alunos.isel.pt", githubToken = "PEaenWxYddN6Q_NT1PiOYfz4EsZu7jRXRlpAsNpBU-A=")

                        // githubUsername
                        on {
                            getPendingUserByGithubId(githubId = 1232)
                        } doReturn PendingTeacher(name = "name", isCreated = false, githubUsername = "test142", githubId = 1111, token = "token", id = 1, email = "test@alunos.isel.pt", githubToken = "PEaenWxYddN6Q_NT1PiOYfz4EsZu7jRXRlpAsNpBU-A=")

                        // GithubId
                        on {
                            getPendingUserByGithubId(githubId = 1233)
                        } doReturn PendingTeacher(name = "name", isCreated = false, githubUsername = "username", githubId = 1233, token = "token", id = 1, email = "test@alunos.isel.pt", githubToken = "PEaenWxYddN6Q_NT1PiOYfz4EsZu7jRXRlpAsNpBU-A=")

                        // token
                        on {
                            getPendingUserByGithubId(githubId = 1234)
                        } doReturn PendingTeacher(name = "name", isCreated = false, githubUsername = "username", githubId = 1236, token = "PEaenWxYddN6Q_NT1PiOYfz4EsZu7jRXRlpAsNpBU-A=", id = 2, email = "test1@alunos.isel.pt", githubToken = "PEaenWxYddN6Q_NT1PiOYfz4EsZu7jRXRlpAsNpBU-A=")

                        // email
                        on {
                            getPendingUserByGithubId(githubId = 1235)
                        } doReturn PendingStudent(name = "name", isCreated = false, githubUsername = "username", githubId = 1111, token = "PEaenWxYddN6Q_NT1PiOYfz4EsZu7jRXRlpAsNpBU-A=", id = 1, email = "test5@alunos.isel.pt")

                        // githubUsername
                        on {
                            getPendingUserByGithubId(githubId = 1236)
                        } doReturn PendingStudent(name = "name", isCreated = false, githubUsername = "test142", githubId = 1111, token = "PEaenWxYddN6Q_NT1PiOYfz4EsZu7jRXRlpAsNpBU-A=", id = 1, email = "test@alunos.isel.pt")

                        // GithubId
                        on {
                            getPendingUserByGithubId(githubId = 1237)
                        } doReturn PendingStudent(name = "name", isCreated = false, githubUsername = "username", githubId = 1237, token = "PEaenWxYddN6Q_NT1PiOYfz4EsZu7jRXRlpAsNpBU-A=", id = 1, email = "test@alunos.isel.pt")

                        // token
                        on {
                            getPendingUserByGithubId(githubId = 1238)
                        } doReturn PendingStudent(name = "name", isCreated = false, githubUsername = "username", githubId = 1236, token = "PEaenWxYddN6Q_NT1PiOYfz4EsZu7jRXRlpAsNpBU-A=", id = 2, email = "test@alunos.isel.pt")

                        on {
                            getPendingUserByGithubId(githubId = 12345)
                        } doReturn PendingStudent(name = "name", email = "test@alunos.isel.pt", githubUsername = "username", token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=", githubId = 12345, isCreated = false, id = 1)

                        on {
                            getPendingUserByGithubId(githubId = 12346)
                        } doReturn PendingTeacher(name = "name", email = "test@alunos.isel.pt", githubUsername = "username", token = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=", githubId = 12346, isCreated = false, id = 1, githubToken = "3z5rC7Zs6q3KT4TLw3H9ZuBNIP5R_EFNqNG4TTHReN4=")

                        on {
                            getUserByGithubId(githubId = 12555L)
                        } doReturn Teacher(name = "teacher2", isCreated = false, githubUsername = "test1234", githubId = 1234, token = "token1", id = 2, email = "test1@alunos.isel.pt")

                        on {
                            getUserByToken(token = "bearer")
                        } doReturn Teacher(name = "teacher2", isCreated = false, githubUsername = "test1234", githubId = 1234, token = "token1", id = 2, email = "test1@alunos.isel.pt")

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
                    on { usersRepository } doReturn mockedUsersRepository
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

        if (user is Either.Left) {
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
        if (user is Either.Left) {
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
        if (user is Either.Right) {
            assert(user.value.name == name)
        } else {
            fail("User not found")
        }
    }

    // TEST: checkAuthentication

    @Test
    fun `checkAuthentication should give an InvalidBearerToken because the InvalidBearerToken is invalid`() {
        // given: an invalid bearer token
        val bearerToken = ""

        // when: getting an error because of an invalid github id
        val user = userServices.checkAuthentication(token = bearerToken)

        if (user is Either.Left) {
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
        if (user is Either.Left) {
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
        if (user is Either.Right) {
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

        if (courses is Either.Left) {
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

        if (courses is Either.Right) {
            assert(courses.value.size == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: getCourses

    @Test
    fun `getCourses should give an InvalidData because the teacherId is invalid`() {
        // given: an invalid teacher id
        val teacherId = -1

        // when: getting an error because of an invalid teacher id
        val user = userServices.getAllUserCourses(userId = teacherId)

        if (user is Either.Left) {
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

        if (user is Either.Right) {
            assert(user.value.size == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: createStudent

    @Test
    fun `createPendingStudent should be InvalidData because the student name is empty`() {
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
        if (student is Either.Left) {
            assert(student.value is StudentServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingStudent should be InvalidData because the student email is empty`() {
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
        if (student is Either.Left) {
            assert(student.value is StudentServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingStudent should be InvalidData because the teacher schoolId is invalid`() {
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
        if (student is Either.Left) {
            assert(student.value is StudentServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingStudent should be InvalidData because the student githubUsername is empty`() {
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
        if (student is Either.Left) {
            assert(student.value is StudentServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingStudent should be InvalidData because the student githubId is invalid`() {
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
        if (student is Either.Left) {
            assert(student.value is StudentServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingStudent should be InvalidData because the student token is invalid`() {
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
        if (student is Either.Left) {
            assert(student.value is StudentServicesError.InvalidData)
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
        if (student is Either.Right) {
            assert(student.value.id == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }

    @Test
    fun `createStudent should be InvalidData because the student githubId in invalid`() {
        // given: a student githubId
        val githubId = -1L

        // when: creating a student should give an error becausee the student githubId in invalid
        val student = studentServices.createStudent(
            githubId = githubId,
            schoolId = 222,
        )

        // the result should be an error
        if (student is Either.Left) {
            assert(student.value is StudentServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be InvalidData because the student schoolId in invalid`() {
        // given: a student schoolId
        val schoolId = -1

        // when: creating a student should give an error becausee the student schoolId in invalid
        val student = studentServices.createStudent(
            githubId = 123,
            schoolId = schoolId,
        )

        // the result should be an error
        if (student is Either.Left) {
            assert(student.value is StudentServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be EmailInUse because the student email is already in use`() {
        // given: a student githubId
        val githubId = 1235L

        // when: creating a student should give an error because of an email already in use
        val student = studentServices.createStudent(
            githubId = githubId,
            schoolId = 222,
        )

        // the result should be an error
        if (student is Either.Left) {
            assert(student.value is StudentServicesError.EmailInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be GithubIdInUse because the student github id is already in use`() {
        // given: a student GitHubId
        val githubId = 1237L

        // when: creating a student should give an error because of GitHub id already in use
        val student = studentServices.createStudent(
            githubId = githubId,
            schoolId = 222,
        )

        // the result should be an error
        if (student is Either.Left) {
            assert(student.value is StudentServicesError.GithubIdInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be GithubUserNameInUse because the student github username is already in use`() {
        // given: a student GitHub username
        val githubId = 1236L

        // when: creating a student should give an error because of GitHub username already in use
        val student = studentServices.createStudent(
            githubId = githubId,
            schoolId = 222,
        )

        // the result should be an error
        if (student is Either.Left) {
            assert(student.value is StudentServicesError.GithubUserNameInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be TokenInUse because the student token is already in use`() {
        // given: a student token
        val githubId = 1238L

        // when: creating a student should give an error because of token already in use
        val student = studentServices.createStudent(
            githubId = githubId,
            schoolId = 222,
        )

        // the result should be an error
        if (student is Either.Left) {
            assert(student.value is StudentServicesError.TokenInUse)
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
        if (student is Either.Left) {
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
        if (studentRes is Either.Right) {
            assert(studentRes.value.id == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: createTeacher

    @Test
    fun `createPendingTeacher should be InvalidData because the teacher name is empty`() {
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
        if (teacher is Either.Left) {
            assert(teacher.value is TeacherServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should be InvalidData because the teacher email is empty`() {
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
        if (teacher is Either.Left) {
            assert(teacher.value is TeacherServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should be InvalidData because the teacher githubToken is empty`() {
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
        if (teacher is Either.Left) {
            assert(teacher.value is TeacherServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should be InvalidData because the teacher githubUsername is empty`() {
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
        if (teacher is Either.Left) {
            assert(teacher.value is TeacherServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should be InvalidData because the teacher githubId is invalid`() {
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
        if (teacher is Either.Left) {
            assert(teacher.value is TeacherServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createPendingTeacher should be InvalidData because the teacher token is invalid`() {
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
        if (teacher is Either.Left) {
            assert(teacher.value is TeacherServicesError.InvalidData)
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
        if (teacher is Either.Right) {
            assert(teacher.value.id == 1)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should be EmailInUse because the teacher email is already in use`() {
        // given: a githubId email
        val githubId = 1231L

        // when: creating a teacher should give an error because of an email already in use
        val teacher = teacherServices.createTeacher(
            githubId = githubId,
        )

        // the result should be an error
        if (teacher is Either.Left) {
            assert(teacher.value is TeacherServicesError.EmailInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should be GithubIdInUse because the teacher github id is already in use`() {
        // given: a teacher githubId
        val githubId = 1233L

        // when: creating a teacher should give an error because of GitHub id already in use
        val teacher = teacherServices.createTeacher(
            githubId = githubId,
        )

        // the result should be an error
        if (teacher is Either.Left) {
            assert(teacher.value is TeacherServicesError.GithubIdInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should be GithubUserNameInUse because the teacher github username is already in use`() {
        // given: a githubId
        val githubId = 1232L

        // when: creating a teacher should give an error because of GitHub username already in use
        val teacher = teacherServices.createTeacher(
            githubId = githubId,
        )

        // the result should be an error
        if (teacher is Either.Left) {
            assert(teacher.value is TeacherServicesError.GithubUserNameInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should be TokenInUse because the student token is already in use`() {
        // given: a student token
        val githubId = 1234L

        // when: creating a teacher should give an error because of token already in use
        val teacher = teacherServices.createTeacher(
            githubId = githubId,
        )

        // the result should be an error
        if (teacher is Either.Left) {
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
        if (teacherRes is Either.Right) {
            assert(teacherRes.value.id == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }
}
