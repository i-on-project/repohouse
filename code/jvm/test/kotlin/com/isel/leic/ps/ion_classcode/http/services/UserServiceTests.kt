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
                            createStudent(student = StudentInput(name = "test1245", email = "test5@alunos.isel.pt", githubUsername = "test1a23", token = "token5", githubId = 124345))
                        } doReturn 15
                        on { updateStudentSchoolId(userId = 5, schoolId = 1256) }
                        on {
                            createTeacher(teacher = TeacherInput(name = "test142", email = "test5@alunos.isel.pt", githubUsername = "test1239", githubToken = "token5", githubId = 123415, token = "token5"))
                        } doReturn 16
                        on {
                            getStudent(studentId = 4)
                        } doReturn Student(name = "student2", token = "token3", githubId = 1234152, githubUsername = "test12345", isCreated = false, email = "test3@alunos.isel.pt", id = 4, schoolId = 1235)
                        on {
                            getTeacher(teacherId = 2)
                        } doReturn Teacher(name = "teacher2", isCreated = false, githubUsername = "test1234", githubId = 123452, token = "token1", id = 2, email = "test1@alunos.isel.pt")
                    }
                    on { usersRepository } doReturn mockedUsersRepository
                }
                return block(mockedTransaction)
            }
        }
    }

    @Test
    fun `getUserById should give an InvalidData`() {
        // given: an invalid user id
        val userId = -1

        // when: getting an error because of an invalid user id
        val user = userServices.getUserById(userId = userId)

        assert(user is Either.Left)
    }
}
