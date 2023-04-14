package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
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

    @TestConfiguration
    class Config {
        @Bean
        fun transactionManager(): TransactionManager = object : TransactionManager {
            override fun <R> run(block: (Transaction) -> R): R {
                val mockedTransaction = mock<Transaction> {
                    val mockedUsersRepository = mock<UsersRepository> {
                        on {
                            createStudent(student = StudentInput(name = "test1245", email = "test@alunos.isel.pt", githubUsername = "test1a23", token = "qh9vcITUAw4J9VQqoi55R6gc7ClmolHadIz7UBrObdw=", githubId = 124345))
                        } doReturn Student(name = "test1245", email = "test@alunos.isel.pt", githubUsername = "test1a23", token = "token5", githubId = 124345, isCreated = false, id = 3, schoolId = null)

                        on {
                            updateStudentSchoolId(userId = 5, schoolId = 1256)
                        } doAnswer {}

                        on {
                            createTeacher(
                                teacher = TeacherInput(name = "test142", email = "test@alunos.isel.pt", githubUsername = "test1239", githubToken = "qh9vcITUAw4J9VQqoi55R6gc7ClmolHadIz7UBrObdw=", githubId = 123415, token = "qh9vcITUAw4J9VQqoi55R6gc7ClmolHadIz7UBrObdw="),
                            )
                        } doReturn Teacher(name = "test142", id = 2, email = "test@alunos.isel.pt", githubUsername = "test1239", githubId = 123415, token = "token5", isCreated = false)

                        on {
                            getStudent(studentId = 4)
                        } doReturn Student(name = "student2", token = "token3", githubId = 1234152, githubUsername = "test12345", isCreated = false, email = "test3@alunos.isel.pt", id = 4, schoolId = 1235)

                        on {
                            getTeacher(teacherId = 2)
                        } doReturn Teacher(name = "teacher2", isCreated = false, githubUsername = "test1234", githubId = 123452, token = "token1", id = 2, email = "test1@alunos.isel.pt")

                        on {
                            getUserById(id = 4)
                        } doReturn Student(name = "student2", token = "token3", githubId = 1234152, githubUsername = "test12345", isCreated = false, email = "test3@alunos.isel.pt", id = 4, schoolId = 1235)

                        on {
                            getUserByGithubId(githubId = 1234)
                        } doReturn Teacher(name = "teacher2", isCreated = false, githubUsername = "test1234", githubId = 1234, token = "token1", id = 2, email = "test1@alunos.isel.pt")
                        on {
                            getUserByToken(token = "JFStYcKswE8KIPq_fyvJbyjRnENAUv3v27UbRv5TT4k=")
                        } doReturn Teacher(name = "teacher2", isCreated = false, githubUsername = "test1234", githubId = 1234, token = "token1", id = 2, email = "test1@alunos.isel.pt")

                        on {
                            checkIfEmailExists(email = "test5@alunos.isel.pt")
                        } doReturn true

                        on {
                            checkIfGithubIdExists(githubId = 1234)
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

    // TEST: getUserById

    @Test
    fun `getUserById should give an InvalidData because the userId is invalid`() {
        // given: an invalid user id
        val userId = -1

        // when: getting an error because of an invalid user id
        val user = userServices.getUserById(userId = userId)

        if (user is Either.Left) {
            assert(user.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getUserById should give an null because the user doesn't exists`() {
        // given: a valid user id that is not in the database
        val userId = 5

        // when: getting an error because the userId doesn't exists in the database
        val user = userServices.getUserById(userId = userId)

        // the result should be an error
        if (user is Either.Left) {
            assert(user.value is UserServicesError.UserNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getUserById should give an user`() {
        val name = "student2"
        // given: a valid user id that is in the database
        val userId = 4

        // when: getting an error because of an invalid user id
        val user = userServices.getUserById(userId = userId)

        // the result should be an user
        if (user is Either.Right) {
            assert(user.value.name == name)
        } else {
            fail("User not found")
        }
    }

    // TEST: createStudent

    @Test
    fun `createStudent should be InvalidData because the student name is empty`() {
        // given: an invalid student name
        val name = ""

        // when: creating a student should give an error because of an invalid student name
        val student = userServices.createStudent(
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
            assert(student.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be InvalidData because the student email is empty`() {
        // given: an invalid student email
        val email = ""

        // when: creating a student should give an error because of an invalid student email
        val student = userServices.createStudent(
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
            assert(student.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be InvalidData because the teacher schoolId is invalid`() {
        // given: an invalid student schoolId
        val schoolId = -1

        // when: creating a student should give an error because of an invalid student schoolId
        val student = userServices.createStudent(
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
            assert(student.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be InvalidData because the student githubUsername is empty`() {
        // given: an invalid student githubUsername
        val githubUsername = ""

        // when: creating a student should give an error because of an invalid student githubUsername
        val student = userServices.createStudent(
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
            assert(student.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be InvalidData because the student githubId is invalid`() {
        // given: an invalid student githubId
        val githubId = -1L

        // when: creating a student should give an error because of an invalid student githubId
        val student = userServices.createStudent(
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
            assert(student.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be InvalidData because the student token is invalid`() {
        // given: an invalid student token
        val token = ""

        // when: creating a student should give an error because of an invalid student token
        val student = userServices.createStudent(
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
            assert(student.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be EmailInUse because the student email is already in use`() {
        // given: a student email
        val email = "test5@alunos.isel.pt"

        // when: creating a student should give an error because of an email already in use
        val student = userServices.createStudent(
            student = StudentInput(
                name = "name",
                email = email,
                githubUsername = "username",
                githubId = 12345,
                token = "token12",
            ),
        )

        // the result should be an error
        if (student is Either.Left) {
            assert(student.value is UserServicesError.EmailInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be GithubIdInUse because the student github id is already in use`() {
        // given: a student GitHub username
        val githubId = 1234L

        // when: creating a student should give an error because of GitHub id already in use
        val student = userServices.createStudent(
            student = StudentInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "username",
                githubId = githubId,
                token = "token12",
            ),
        )

        // the result should be an error
        if (student is Either.Left) {
            assert(student.value is UserServicesError.GithubIdInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be GithubUserNameInUse because the student github username is already in use`() {
        // given: a student GitHub username
        val githubUsername = "test142"

        // when: creating a student should give an error because of GitHub username already in use
        val student = userServices.createStudent(
            student = StudentInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = githubUsername,
                githubId = 12345L,
                token = "token12",
            ),
        )

        // the result should be an error
        if (student is Either.Left) {
            assert(student.value is UserServicesError.GithubUserNameInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be TokenInUse because the student token is already in use`() {
        // given: a student token
        val token = "token"

        // when: creating a student should give an error because of token already in use
        val student = userServices.createStudent(
            student = StudentInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "githubUsername",
                githubId = 12345L,
                token = token,
            ),
        )

        // the result should be an error
        if (student is Either.Left) {
            assert(student.value is UserServicesError.TokenInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should be SchoolIdInUse because the student school id is already in use`() {
        // given: a student school id
        val schoolId = 1235

        // when: creating a student should give an error because of school id already in use
        val student = userServices.createStudent(
            student = StudentInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "githubUsername",
                githubId = 12345L,
                token = "token12",
                schoolId = schoolId,
            ),
        )

        // the result should be an error
        if (student is Either.Left) {
            assert(student.value is UserServicesError.SchoolIdInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createStudent should return the new student`() {
        // given: a valid student
        val student = StudentInput(
            name = "test1245",
            email = "test@alunos.isel.pt",
            githubUsername = "test1a23",
            token = "token5",
            githubId = 124345,
        )

        // when: creating a student should give a student
        val studentRes = userServices.createStudent(student = student)

        // the result should be a student
        if (studentRes is Either.Right) {
            assert(studentRes.value.id == 3)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: createTeacher

    @Test
    fun `createTeacher should be InvalidData because the teacher name is empty`() {
        // given: an invalid teacher name
        val name = ""

        // when: creating a teacher should give an error because of an invalid teacher name
        val teacher = userServices.createTeacher(
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
            assert(teacher.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should be InvalidData because the teacher email is empty`() {
        // given: an invalid teacher email
        val email = ""

        // when: creating a teacher should give an error because of an invalid teacher email
        val teacher = userServices.createTeacher(
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
            assert(teacher.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should be InvalidData because the teacher githubToken is empty`() {
        // given: an invalid teacher githubToken
        val githubToken = ""

        // when: creating a teacher should give an error because of an invalid teacher githubToken
        val teacher = userServices.createTeacher(
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
            assert(teacher.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should be InvalidData because the teacher githubUsername is empty`() {
        // given: an invalid teacher githubUsername
        val githubUsername = ""

        // when: creating a teacher should give an error because of an invalid teacher githubUsername
        val teacher = userServices.createTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = githubUsername,
                githubToken = "token",
                githubId = 12341,
                token = "token",
            ),
        )

        // the result should be an error
        if (teacher is Either.Left) {
            assert(teacher.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should be InvalidData because the teacher githubId is invalid`() {
        // given: an invalid teacher githubId
        val githubId = -1L

        // when: creating a teacher should give an error because of an invalid teacher githubId
        val teacher = userServices.createTeacher(
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
            assert(teacher.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should be InvalidData because the teacher token is invalid`() {
        // given: an invalid teacher token
        val token = ""

        // when: creating a teacher should give an error because of an invalid teacher token
        val teacher = userServices.createTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "username",
                githubToken = "token",
                githubId = 12345,
                token = token,
            ),
        )

        // the result should be an error
        if (teacher is Either.Left) {
            assert(teacher.value is UserServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should be EmailInUse because the teacher email is already in use`() {
        // given: a teacher email
        val email = "test5@alunos.isel.pt"

        // when: creating a teacher should give an error because of an email already in use
        val teacher = userServices.createTeacher(
            teacher = TeacherInput(
                name = "name",
                email = email,
                githubUsername = "username",
                githubId = 12345,
                token = "token12",
                githubToken = "token1",
            ),
        )

        // the result should be an error
        if (teacher is Either.Left) {
            assert(teacher.value is UserServicesError.EmailInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should be GithubIdInUse because the teacher github id is already in use`() {
        // given: a teacher GitHub username
        val githubId = 1234L

        // when: creating a teacher should give an error because of GitHub id already in use
        val teacher = userServices.createTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "username",
                githubId = githubId,
                token = "token12",
                githubToken = "token1",
            ),
        )

        // the result should be an error
        if (teacher is Either.Left) {
            assert(teacher.value is UserServicesError.GithubIdInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should be GithubUserNameInUse because the teacher github username is already in use`() {
        // given: a teacher GitHub username
        val githubUsername = "test142"

        // when: creating a teacher should give an error because of GitHub username already in use
        val teacher = userServices.createTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = githubUsername,
                githubId = 12345L,
                token = "token12",
                githubToken = "token1",
            ),
        )

        // the result should be an error
        if (teacher is Either.Left) {
            assert(teacher.value is UserServicesError.GithubUserNameInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should be TokenInUse because the student token is already in use`() {
        // given: a student token
        val token = "token"

        // when: creating a teacher should give an error because of token already in use
        val teacher = userServices.createTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "githubUsername",
                githubId = 12345L,
                token = token,
                githubToken = "token1",
            ),
        )

        // the result should be an error
        if (teacher is Either.Left) {
            assert(teacher.value is UserServicesError.TokenInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should be SchoolIdInUse because theGitHub token id is already in use`() {
        // given: a teacher GitHub token
        val githubToken = "token"

        // when: creating a teacher should give an error because of GitHub token already in use
        val teacher = userServices.createTeacher(
            teacher = TeacherInput(
                name = "name",
                email = "test@alunos.isel.pt",
                githubUsername = "githubUsername",
                githubId = 12345L,
                token = "token12",
                githubToken = githubToken,
            ),
        )

        // the result should be an error
        if (teacher is Either.Left) {
            assert(teacher.value is UserServicesError.GithubTokenInUse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createTeacher should return the new teacher`() {
        // given: a valid teacher
        val teacher = TeacherInput(
            name = "test142",
            email = "test@alunos.isel.pt",
            githubUsername = "test1239",
            githubToken = "token5",
            githubId = 123415,
            token = "token5",
        )

        // when: creating a teacher should give a teacher
        val teacheRes = userServices.createTeacher(teacher = teacher)

        // the result should be a teacher
        if (teacheRes is Either.Right) {
            assert(teacheRes.value.id == 2)
        } else {
            fail("Should not be Either.Left")
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
            assert(user.value is UserServicesError.InvalidGithubId)
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
        val githubId = 1234L

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
}
