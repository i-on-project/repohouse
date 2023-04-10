package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.http.model.input.CourseInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseArchivedModel
import com.isel.leic.ps.ion_classcode.repository.CourseRepository
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
import java.sql.Timestamp
import java.time.Instant

@SpringBootTest
class CourseServiceTests {

    companion object {
        val teacher = Teacher(id = 1, name = "Teacher", email = "test@gmail", githubId = 123, githubUsername = "githubUsername", token = "token", isCreated = false)
        val student = Student(name = "test", email = "test@alunos.isel.pt", githubUsername = "test", token = "token2", githubId = 124345, isCreated = false, id = 2, schoolId = null)
    }

    @Autowired
    lateinit var courseService: CourseServices

    @TestConfiguration
    class Config {
        @Bean
        fun transactionManager(): TransactionManager = object : TransactionManager {
            override fun <R> run(block: (Transaction) -> R): R {
                val mockedTransaction = mock<Transaction> {
                    val mockedUsersRepository = mock<UsersRepository> {
                        on { getTeacher(teacherId = 1) } doReturn teacher
                        on { getUserById(id = 1) } doReturn teacher
                        on { getUserById(id = 2) } doReturn student
                    }
                    val mockedCourseRepository = mock<CourseRepository> {
                        on {
                            createCourse(course = CourseInput(orgUrl = "orgUrl3", name = "courseName3", teacherId = 1))
                        } doReturn Course(id = 2, orgUrl = "orgUrl1", name = "courseName1", teachers = listOf<Teacher>(teacher), isArchived = false)
                        on { getCourse(courseId = 1) } doReturn Course(id = 1, orgUrl = "orgUrl", name = "courseName", teachers = listOf<Teacher>(teacher), isArchived = false)
                        on { getCourse(courseId = 2) } doReturn Course(id = 2, orgUrl = "orgUrl1", name = "courseName1", teachers = listOf<Teacher>(teacher), isArchived = true)
                        on { getCourse(courseId = 3) } doReturn Course(id = 3, orgUrl = "orgUrl2", name = "courseName2", teachers = listOf<Teacher>(teacher), isArchived = false)
                        on { getCourseUserClassrooms(courseId = 1, userId = 2) } doReturn listOf<Classroom>(Classroom(id = 1, name = "name", lastSync = Timestamp.from(Instant.now()), courseId = 1, isArchived = false, inviteLink = "inviteLink"))
                        on { getStudentInCourse(courseId = 1) } doReturn listOf<Student>(student)
                        on { getCourseByOrg(orgUrl = "orgUrl") } doReturn Course(id = 2, orgUrl = "orgUrl", name = "courseName", teachers = listOf<Teacher>(teacher), isArchived = false)
                        on {
                            addTeacherToCourse(teacherId = 1, courseId = 2)
                        } doReturn Course(id = 2, orgUrl = "orgUrl1", name = "courseName1", teachers = listOf<Teacher>(teacher, Teacher(name = "teacher1", email = "teacher@gmail", id = 3, githubUsername = "githubUsername2", githubId = 202, token = "token1", isCreated = false)), isArchived = false)
                        on { checkIfCourseNameExists(name = "courseName") } doReturn true

                        on {
                            getCourseAllClassrooms(courseId = 1)
                        } doReturn listOf<Classroom>(Classroom(id = 1, name = "name", lastSync = Timestamp.from(Instant.now()), courseId = 1, isArchived = false, inviteLink = "inviteLink"), Classroom(id = 2, name = "name2", lastSync = Timestamp.from(Instant.now()), courseId = 1, isArchived = false, inviteLink = "inviteLink2"))
                        on {
                            getCourseAllClassrooms(courseId = 3)
                        } doReturn listOf<Classroom>()
                        on { archiveCourse(courseId = 2) } doAnswer { }
                        on { deleteCourse(courseId = 2) } doAnswer { }
                        on { isStudentInCourse(studentId = 1, courseId = 1) } doReturn true
                        on { leaveCourse(courseId = 1, studentId = 1) } doReturn Course(id = 1, orgUrl = "orgUrl", name = "courseName", teachers = listOf<Teacher>(teacher), isArchived = false)
                    }

                    on { usersRepository } doReturn mockedUsersRepository
                    on { courseRepository } doReturn mockedCourseRepository
                }
                return block(mockedTransaction)
            }
        }
    }
    // TEST: getCourseById

    @Test
    fun `getCourseById should give an InvalidInput because the courseId is invalid`() {
        // given: an invalid course id and a valid user id
        val courseId = -1
        val userId = 1
        // when: getting an error because of an invalid course id
        val course = courseService.getCourseById(courseId = courseId, userId = userId)

        if (course is Either.Left) {
            assert(course.value is CourseServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getCourseById should give an InvalidInput because the userId is invalid`() {
        // given: an invalid user id and a valid course id
        val userId = -1
        val courseId = 1

        // when: getting an error because of an invalid user id
        val course = courseService.getCourseById(courseId = courseId, userId = userId)

        if (course is Either.Left) {
            assert(course.value is CourseServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getCourseById should give an CourseNotFound because the the course id does not exist in database`() {
        // given: a valid user id and a valid course id
        val courseId = 4
        val userId = 1

        // when: getting an error because the course id that doesn't exist in database
        val course = courseService.getCourseById(courseId = courseId, userId = userId)

        if (course is Either.Left) {
            assert(course.value is CourseServicesError.CourseNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getCourseById should give an UserNotFound because the the user id does not exist in database`() {
        // given: a valid user id and a valid course id
        val courseId = 1
        val userId = 3

        // when: getting an error because the user id that doesn't exist in database
        val course = courseService.getCourseById(courseId = courseId, userId = userId)

        if (course is Either.Left) {
            assert(course.value is CourseServicesError.UserNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getCourseById should give an course with classrooms`() {
        // given: a valid user id and a valid course id
        val courseId = 1
        val userId = 2

        // when: getting an error because the user id that doesn't exist in database
        val course = courseService.getCourseById(courseId = courseId, userId = userId)

        if (course is Either.Right) {
            assert(course.value.id == courseId)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: createCourse

    @Test
    fun `createCourse should be InvalidInput because the org url is empty`() {
        // given: an invalid org url
        val orgUrl = ""

        // when: creating a course should give an error because of an invalid org url
        val course = courseService.createCourse(
            courseInfo = CourseInputModel(
                orgUrl = orgUrl,
                name = "courseName3",
                teacherId = 1,
            ),
        )

        // the result should be an error
        if (course is Either.Left) {
            assert(course.value is CourseServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createCourse should be InvalidInput because the name is empty`() {
        // given: an invalid name
        val name = ""

        // when: creating a course should give an error because of an invalid name
        val course = courseService.createCourse(
            courseInfo = CourseInputModel(
                orgUrl = "orgUrl2",
                name = name,
                teacherId = 1,
            ),
        )

        // the result should be an error
        if (course is Either.Left) {
            assert(course.value is CourseServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createCourse should be InvalidInput because the teacher id is invalid`() {
        // given: an invalid teacher id
        val teacherId = -1

        // when: creating a course should give an error because of an invalid teacher id
        val course = courseService.createCourse(
            courseInfo = CourseInputModel(
                orgUrl = "orgUrl2",
                name = "name2",
                teacherId = teacherId,
            ),
        )

        // the result should be an error
        if (course is Either.Left) {
            assert(course.value is CourseServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createCourse should be CourseNameAlreadyExists because the name is already in use`() {
        // given: a valid name
        val name = "courseName"

        // when: creating a course should give an error because the name already exists
        val course = courseService.createCourse(
            courseInfo = CourseInputModel(
                orgUrl = "orgUrl2",
                name = name,
                teacherId = 1,
            ),
        )

        // the result should be an error
        if (course is Either.Left) {
            assert(course.value is CourseServicesError.CourseNameAlreadyExists)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createCourse should be NotTeacher because the teacher id don't exists in database`() {
        // given: a valid teacher id
        val teacherId = 4

        // when: creating a course should give an error because the teacher id don't exists in database
        val course = courseService.createCourse(
            courseInfo = CourseInputModel(
                orgUrl = "orgUrl3",
                name = "name",
                teacherId = teacherId,
            ),
        )

        // the result should be an error
        if (course is Either.Left) {
            assert(course.value is CourseServicesError.NotTeacher)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createCourse should add the teacher to the course because the course already exists`() {
        // when: creating a course should add a teacher to a course
        val course = courseService.createCourse(
            courseInfo = CourseInputModel(
                orgUrl = "orgUrl",
                name = "name",
                teacherId = 1,
            ),
        )

        // the result should be an error
        if (course is Either.Right) {
            assert(course.value.teachers.size == 2)
        } else {
            fail("Should not be Either.Left")
        }
    }

    @Test
    fun `createCourse should create a new course`() {
        // when: creating a course should create a course
        val course = courseService.createCourse(
            courseInfo = CourseInputModel(
                orgUrl = "orgUrl3",
                name = "courseName3",
                teacherId = 1,
            ),
        )

        // the result should be an error
        if (course is Either.Right) {
            assert(course.value.id == 2)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: archiveOrDeleteCourse

    @Test
    fun `archiveOrDeleteCourse should be InvalidInput because the course id is invalid`() {
        // given: an invalid course id
        val courseId = -1

        // when: archiving a course should give an error because of an invalid course id
        val course = courseService.archiveOrDeleteCourse(courseId = courseId)

        // the result should be an error
        if (course is Either.Left) {
            assert(course.value is CourseServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `archiveOrDeleteCourse should be CourseNotFound because the course id is not in database`() {
        // given: a valid course id
        val courseId = 4

        // when: archiving a course should give an error because the course id is not in database
        val course = courseService.archiveOrDeleteCourse(courseId = courseId)

        // the result should be an error
        if (course is Either.Left) {
            assert(course.value is CourseServicesError.CourseNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `archiveOrDeleteCourse should be CourseArchived because the course is already archived`() {
        // given: a valid course id
        val courseId = 2

        // when: archiving a course should give an error because the course is already archived
        val course = courseService.archiveOrDeleteCourse(courseId = courseId)

        // the result should be an error
        if (course is Either.Left) {
            assert(course.value is CourseServicesError.CourseArchived)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `archiveOrDeleteCourse should be CourseArchived because the course was successfully archived`() {
        // given: a valid course id
        val courseId = 1

        // when: archiving a course should give a CourseArchivedModel because it have classrooms associated
        val course = courseService.archiveOrDeleteCourse(courseId = courseId)

        // the result should be an CourseArchivedModel
        if (course is Either.Right) {
            assert(course.value is CourseArchivedModel.CourseArchived)
        } else {
            fail("Should not be Either.Left")
        }
    }

    @Test
    fun `archiveOrDeleteCourse should be CourseDeleted because the course was successfully deleted`() {
        // given: a valid course id
        val courseId = 3

        // when: archiving a course should give a CourseArchivedModel because it has not any classrooms associated
        val course = courseService.archiveOrDeleteCourse(courseId = courseId)

        // the result should be an CourseArchivedModel
        if (course is Either.Right) {
            assert(course.value is CourseArchivedModel.CourseDeleted)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: leaveCourse

    @Test
    fun `leaveCourse should give an InvalidInput because the courseId is invalid`() {
        // given: an invalid course id and a valid user id
        val courseId = -1
        val userId = 1
        // when: getting an error because of an invalid course id
        val course = courseService.leaveCourse(courseId = courseId, userId = userId)

        if (course is Either.Left) {
            assert(course.value is CourseServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `leaveCourse should give an InvalidInput because the userId is invalid`() {
        // given: an invalid user id and a valid course id
        val userId = -1
        val courseId = 1

        // when: getting an error because of an invalid user id
        val course = courseService.leaveCourse(courseId = courseId, userId = userId)

        if (course is Either.Left) {
            assert(course.value is CourseServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `leaveCourse should give an CourseNotFound because the the course id does not exist in database`() {
        // given: a valid user id and a valid course id
        val courseId = 4
        val userId = 1

        // when: getting an error because the course id that doesn't exist in database
        val course = courseService.leaveCourse(courseId = courseId, userId = userId)

        if (course is Either.Left) {
            assert(course.value is CourseServicesError.CourseNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `leaveCourse should give an UserNotFound because the the user id does not exist in database`() {
        // given: a valid user id and a valid course id
        val courseId = 1
        val userId = 2

        // when: getting an error because the user id that doesn't exist in database
        val course = courseService.leaveCourse(courseId = courseId, userId = userId)

        if (course is Either.Left) {
            assert(course.value is CourseServicesError.UserNotInCourse)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `leaveCourse should be successeful`() {
        // given: a valid user id and a valid course id
        val courseId = 1
        val userId = 1

        // when: doing with success the leave course
        val course = courseService.leaveCourse(courseId = courseId, userId = userId)

        if (course is Either.Right) {
            assert(course.value.name == "courseName")
        } else {
            fail("Should not be Either.Right")
        }
    }
}
