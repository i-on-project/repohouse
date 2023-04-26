package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.Course
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
                        on { getAllUserCourses(userId = 1) } doReturn listOf(Course(id = 1, orgUrl = "orgUrl", name = "name", teachers = listOf()))
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
    fun `getStudentSchoolId should give an InvalidInput because the studentId is invalid`() {
        // given: an invalid student id
        val studentId = -1

        // when: getting an error because of an invalid student id
        val student = studentServices.getStudentSchoolId(studentId = studentId)

        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getStudentSchoolId should give an UserNotFound because the studentId is not in database`() {
        // given: a valid student id
        val studentId = 2

        // when: getting the school id
        val courses = studentServices.getStudentSchoolId(studentId = studentId)

        if (courses is Result.Problem) {
            assert(courses.value is StudentServicesError.StudentNotFound)
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

    // TEST: updateStudent

    @Test
    fun `updateStudent should give an InvalidInput because the userId is invalid`() {
        // given: an invalid student id
        val userId = -1

        // when: getting an error because of an invalid user id
        val student = studentServices.updateStudent(userId = userId, schoolId = 1234)

        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateStudent should give an InvalidInput because the schoolId is invalid`() {
        // given: an invalid school id
        val schoolId = -1

        // when: getting an error because of an invalid school id
        val student = studentServices.updateStudent(userId = 1, schoolId = schoolId)

        if (student is Result.Problem) {
            assert(student.value is StudentServicesError.InvalidData)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `updateStudent should give the new school id`() {
        // when: getting the school id
        val student = studentServices.updateStudent(userId = 1, schoolId = 1234)

        if (student is Result.Success) {
            assert(student.value)
        } else {
            fail("Should not be Either.Left")
        }
    }
}
