package com.isel.leic.ps.ionClassCode.services

import com.isel.leic.ps.ionClassCode.domain.Assignment
import com.isel.leic.ps.ionClassCode.domain.Classroom
import com.isel.leic.ps.ionClassCode.domain.Course
import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.Teacher
import com.isel.leic.ps.ionClassCode.domain.input.ClassroomInput
import com.isel.leic.ps.ionClassCode.http.model.input.ClassroomUpdateInputModel
import com.isel.leic.ps.ionClassCode.http.model.output.ClassroomArchivedResult
import com.isel.leic.ps.ionClassCode.repository.AssignmentRepository
import com.isel.leic.ps.ionClassCode.repository.ClassroomRepository
import com.isel.leic.ps.ionClassCode.repository.CourseRepository
import com.isel.leic.ps.ionClassCode.repository.UsersRepository
import com.isel.leic.ps.ionClassCode.repository.transaction.Transaction
import com.isel.leic.ps.ionClassCode.repository.transaction.TransactionManager
import com.isel.leic.ps.ionClassCode.utils.Result
import java.sql.Timestamp
import java.time.Instant
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
class ClassroomServiceTests {
    companion object {
        val student = Student(id = 1, name = "Student 1", email = "student@email", githubUsername = "githubUsername", githubId = 123, isCreated = false, token = "token", schoolId = 123)
        val student2 = Student(id = 2, name = "Student 2", email = "student2@email", githubUsername = "githubUsername2", githubId = 1234, isCreated = false, token = "token1", schoolId = 1223)
        val assignment = Assignment(id = 1, description = "Description", releaseDate = Timestamp.from(Instant.now()), classroomId = 1, maxElemsPerGroup = 3, maxNumberGroups = 3, title = "Title")
    }

    @Autowired
    lateinit var classroomServices: ClassroomServices

    @TestConfiguration
    class Config {
        @Bean
        fun transactionManager(): TransactionManager = object : TransactionManager {
            override fun <R> run(block: (Transaction) -> R): R {
                val mockedTransaction = mock<Transaction> {
                    val mockedClassroomRepository = mock<ClassroomRepository> {
                        on { getClassroomById(classroomId = 1) } doReturn Classroom(id = 1, name = "Classroom 1", inviteLink = "inviteLink", isArchived = false, lastSync = Timestamp.from(Instant.now()), courseId = 1)
                        on { getClassroomById(classroomId = 2) } doReturn Classroom(id = 2, name = "Classroom 2", inviteLink = "inviteLink1", isArchived = true, lastSync = Timestamp.from(Instant.now()), courseId = 1)
                        on { getClassroomById(classroomId = 3) } doReturn Classroom(id = 3, name = "Classroom 3", inviteLink = "inviteLink2", isArchived = false, lastSync = Timestamp.from(Instant.now()), courseId = 1)
                        on { getStudentsByClassroom(classroomId = 1) } doReturn listOf(student)
                        on { getStudentsByClassroom(classroomId = 3) } doReturn listOf(student2)
                        on { getAllInviteLinks() } doReturn listOf("inviteLink", "inviteLink1")
                        on { deleteClassroom(classroomId = 1) } doAnswer {}
                        on { archiveClassroom(classroomId = 1) } doAnswer {}
                        on { updateClassroomName(classroomId = 1, classroomUpdate = ClassroomUpdateInputModel(name = "newName")) } doAnswer {}
                        on {
                            createClassroom(classroom = ClassroomInput(name = "name", courseId = 1, teacherId = 1), inviteLink = "")
                        } doReturn Classroom(id = 2, name = "Classroom 2", inviteLink = "inviteLink2", isArchived = true, lastSync = Timestamp.from(Instant.now()), courseId = 1)
                        on {
                            getClassroomByInviteLink(inviteLink = "inviteLink2")
                        } doReturn Classroom(id = 2, name = "Classroom 2", inviteLink = "inviteLink2", isArchived = true, lastSync = Timestamp.from(Instant.now()), courseId = 1)
                        on {
                            getClassroomByInviteLink(inviteLink = "inviteLink")
                        } doReturn Classroom(id = 3, name = "Classroom 3", inviteLink = "inviteLink", isArchived = false, lastSync = Timestamp.from(Instant.now()), courseId = 1)

                        on { addStudentToClassroom(classroomId = 1, studentId = 4) } doAnswer {}
                    }
                    val mockedAssignmentRepository = mock<AssignmentRepository> {
                        on { getClassroomAssignments(classroomId = 1) } doReturn listOf(assignment)
                        on { getClassroomAssignments(classroomId = 2) } doReturn listOf(assignment)
                    }
                    val mockedUsersRepository = mock<UsersRepository> {
                        on {
                            getTeacher(teacherId = 1)
                        } doReturn Teacher(name = "teacher2", isCreated = false, githubUsername = "test1234", githubId = 123452, token = "token1", id = 2, email = "test1@alunos.isel.pt")
                        on {
                            getStudent(studentId = 3)
                        } doReturn Student(name = "student3", email = "email3", id = 3, githubUsername = "test1234", githubId = 123452, token = "token1", isCreated = true, schoolId = 48309)
                        on {
                            getStudent(studentId = 1)
                        } doReturn student
                        on {
                            getStudent(studentId = 2)
                        } doReturn student2
                    }
                    val mockedCoursesRepository = mock<CourseRepository> {
                        on {
                            getCourse(courseId = 1)
                        } doReturn Course(id = 1, orgUrl = "orgUrl", name = "name", orgId = 1111, teachers = listOf())
                    }
                    on { classroomRepository } doReturn mockedClassroomRepository
                    on { assignmentRepository } doReturn mockedAssignmentRepository
                    on { courseRepository } doReturn mockedCoursesRepository
                    on { usersRepository } doReturn mockedUsersRepository
                }
                return block(mockedTransaction)
            }
        }
    }

    // TEST: getClassroom

    @Test
    fun `getClassroom should give an ClassroomNotFound because the classroomId is invalid`() {
        // given: an invalid classroom id
        val classroomId = -1

        // when: getting an error because of an invalid classroom id
        val classroom = classroomServices.getClassroom(classroomId = classroomId)

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getClassroom should give an ClassroomNotFound because the classroomId is not in database`() {
        // given: a valid classroom id
        val classroomId = 4

        // when: getting an error because the classroom id is not in database
        val classroom = classroomServices.getClassroom(classroomId = classroomId)

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `getClassroom should give a classroom`() {
        // given: an invalid classroom id
        val classroomId = 1

        // when: getting the classroom
        val classroom = classroomServices.getClassroom(classroomId = classroomId)

        if (classroom is Result.Success) {
            assert(classroom.value.name == "Classroom 1")
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: createClassroom

    @Test
    fun `createClassroom should give an InvalidInput because the name invalid`() {
        // given: an invalid name
        val name = ""

        // when: getting an error because of an invalid name
        val classroom = classroomServices.createClassroom(
            classroomInput = ClassroomInput(name = name, courseId = 1, teacherId = 1),
        )

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createClassroom should give an CourseNotFound because the course id invalid`() {
        // given: an invalid course id
        val courseId = -1

        // when: getting an error because of an invalid course id
        val classroom = classroomServices.createClassroom(
            classroomInput = ClassroomInput(name = "name", courseId = courseId, teacherId = 1),
        )

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.CourseNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `createClassroom should give an InternalError because the teacher id invalid`() {
        // given: an invalid teacher id
        val teacherId = -1

        // when: getting an error because of an invalid teacher id
        val classroom = classroomServices.createClassroom(
            classroomInput = ClassroomInput(name = "name", courseId = 1, teacherId = teacherId),
        )

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    // TEST: archiveOrDeleteClassroom

    @Test
    fun `archiveOrDeleteClassroom should give an ClassroomNotFound because the classroomId is invalid`() {
        // given: an invalid classroom id
        val classroomId = -1

        // when: getting an error because of an invalid classroom id
        val classroom = classroomServices.archiveOrDeleteClassroom(classroomId = classroomId, creator = 1)

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `archiveOrDeleteClassroom should give an ClassroomNotFound because the classroomId is not in database`() {
        // given: an invalid classroom id
        val classroomId = 4

        // when: getting an error because classroom id does not exists
        val classroom = classroomServices.archiveOrDeleteClassroom(classroomId = classroomId, creator = 1)

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `archiveOrDeleteClassroom should give an ClassroomArchived because the is already archived`() {
        // given: a valid classroom id
        val classroomId = 2

        // when: getting a ClassroomArchived
        val classroom = classroomServices.archiveOrDeleteClassroom(classroomId = classroomId, creator = 1)

        if (classroom is Result.Success) {
            assert(classroom.value is ClassroomArchivedResult.ClassroomArchived)
        } else {
            fail("Should not be Either.Left")
        }
    }

    @Test
    fun `archiveOrDeleteClassroom should give an ClassroomArchived because it was successful archived`() {
        // given: a valid classroom id
        val classroomId = 1

        // when: getting a ClassroomArchived
        val classroom = classroomServices.archiveOrDeleteClassroom(classroomId = classroomId, creator = 1)

        if (classroom is Result.Success) {
            assert(classroom.value is ClassroomArchivedResult.ClassroomArchived)
        } else {
            fail("Should not be Either.Left")
        }
    }

    @Test
    fun `archiveOrDeleteClassroom should give an ClassroomDeleted because it was successful deleted`() {
        // given: a valid classroom id
        val classroomId = 3

        // when: getting a ClassroomDeleted
        val classroom = classroomServices.archiveOrDeleteClassroom(classroomId = classroomId, creator = 1)

        if (classroom is Result.Success) {
            assert(classroom.value is ClassroomArchivedResult.ClassroomDeleted)
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: editClassroom

    @Test
    fun `editClassroom should give an InvalidInput because the name invalid`() {
        // given: an invalid name
        val name = ""

        // when: getting an error because of an invalid name
        val classroom = classroomServices.editClassroom(
            classroomId = 1,
            classroomUpdateInput = ClassroomUpdateInputModel(name = name),
        )

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `editClassroom should give an ClassroomNotFound because the classroom id is invalid`() {
        // given: an invalid classroom id
        val classroomId = -1

        // when: getting an error because of an invalid name
        val classroom = classroomServices.editClassroom(
            classroomId = classroomId,
            classroomUpdateInput = ClassroomUpdateInputModel(name = "name"),
        )

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `editClassroom should give an ClassroomNotFound because the classroom id is not in database`() {
        // given: a valid classroom id
        val classroomId = 4

        // when: getting an error because the classroom id is not in database
        val classroom = classroomServices.editClassroom(
            classroomId = classroomId,
            classroomUpdateInput = ClassroomUpdateInputModel(name = "name"),
        )

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.ClassroomNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `editClassroom should give an ClassroomArchived because the classroom is already archived`() {
        // given: a valid classroom id
        val classroomId = 2

        // when: getting an error because the classroom is already archived
        val classroom = classroomServices.editClassroom(
            classroomId = classroomId,
            classroomUpdateInput = ClassroomUpdateInputModel(name = "name"),
        )

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.ClassroomArchived)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `editClassroom should give the classroom updated`() {
        // given: a valid classroom id
        val classroomId = 1

        // when: getting the classroom updated
        val classroom = classroomServices.editClassroom(
            classroomId = classroomId,
            classroomUpdateInput = ClassroomUpdateInputModel(name = "newName"),
        )

        if (classroom is Result.Success) {
            assert(classroom.value.name == "newName")
        } else {
            fail("Should not be Either.Left")
        }
    }

    // TEST: enterClassroomWithInvite
    @Test
    fun `enterClassroomWithInvite should give an InvalidInput because the invite link invalid`() {
        // given: an invalid invite link name
        val inviteLink = ""

        // when: getting an error because of an invalid invite link
        val classroom = classroomServices.enterClassroomWithInvite(inviteLink = inviteLink, studentId = 1)

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.InvalidInput)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `enterClassroomWithInvite should give an InternalError because the student id is invalid`() {
        // given: an invalid student id
        val studentId = -1

        // when: getting an error because of an invalid student id
        val classroom = classroomServices.enterClassroomWithInvite(inviteLink = "inviteLink", studentId = studentId)

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.InternalError)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `enterClassroomWithInvite should give an InviteLinkNotFound because the invite link is not in database`() {
        // given: a valid invite link name
        val inviteLink = "inviteLink7"

        // when: getting an error because the invite link that is not in database
        val classroom = classroomServices.enterClassroomWithInvite(inviteLink = inviteLink, studentId = 1)

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.InviteLinkNotFound)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `enterClassroomWithInvite should give an ClassroomArchived because the classroom is archived`() {
        // given: a valid invite link
        val inviteLink = "inviteLink2"

        // when: getting an error because the classroom is archived
        val classroom = classroomServices.enterClassroomWithInvite(inviteLink = inviteLink, studentId = 1)

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.ClassroomArchived)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `enterClassroomWithInvite should give an AlreadyInClassroom if the student is already in the classroom`() {
        // given: a valid invite link name
        val studentId = 2

        // when: getting an error because the classroom is archived
        val classroom = classroomServices.enterClassroomWithInvite(inviteLink = "inviteLink", studentId = studentId)

        if (classroom is Result.Problem) {
            assert(classroom.value is ClassroomServicesError.AlreadyInClassroom)
        } else {
            fail("Should not be Either.Right")
        }
    }

    @Test
    fun `enterClassroomWithInvite should give the classroom with the new student`() {
        // given: a valid invite link name
        val studentId = 3

        // when: getting the classroom with the new student
        val classroom = classroomServices.enterClassroomWithInvite(inviteLink = "inviteLink", studentId = studentId)

        if (classroom is Result.Success) {
            assert(classroom.value.classroom.students.size == 1)
        } else {
            fail("Should not be Either.Left")
        }
    }
}
