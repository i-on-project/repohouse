package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.input.AssignmentInput
import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiAssignmentRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiClassroomRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiCourseRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiUsersRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.Instant

class AssignmentRepositoryTests {
    @Test
    fun `can create a assignment`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val assignmentRepo = JdbiAssignmentRepository(handle = handle)
        val userId = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        val courseId = courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = userId))
        val classroomId = classroomRepo.createClassroom(classroom = ClassroomInput(name = "Classroom 1", courseId = courseId, inviteLink = "linking link"))
        assignmentRepo.createAssignment(
            assignment = AssignmentInput(
                classroomId = classroomId,
                maxNumberElems = 10,
                maxNumberGroups = 10,
                releaseDate = Timestamp.from(Instant.now()),
                description = "aaaa",
                title = "aaa",
            ),
        )
    }
    @Test
    fun `can get a assignment`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val assignmentRepo = JdbiAssignmentRepository(handle = handle)
        val userId = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        val courseId = courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = userId))
        val classroomId = classroomRepo.createClassroom(classroom = ClassroomInput(name = "Classroom 1", courseId = courseId, inviteLink = "linking link"))
        val assignmentId = assignmentRepo.createAssignment(
            assignment = AssignmentInput(
                classroomId = classroomId,
                maxNumberElems = 10,
                maxNumberGroups = 10,
                releaseDate = Timestamp.from(Instant.now()),
                description = "aaaa",
                title = "aaa",
            ),
        )
        val assignment = assignmentRepo.getAssignmentById(assignmentId = assignmentId)
        assert(assignment != null)
    }

    @Test
    fun `can delete a assignment`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val assignmentRepo = JdbiAssignmentRepository(handle = handle)
        val userId = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        val courseId = courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = userId))
        val classroomId = classroomRepo.createClassroom(classroom = ClassroomInput(name = "Classroom 1", courseId = courseId, inviteLink = "linking link"))
        val assignmentId = assignmentRepo.createAssignment(
            assignment = AssignmentInput(
                classroomId = classroomId,
                maxNumberElems = 10,
                maxNumberGroups = 10,
                releaseDate = Timestamp.from(Instant.now()),
                description = "aaaa",
                title = "aaa",
            ),
        )
        assignmentRepo.deleteAssignment(assignmentId = assignmentId)
        val assignment = assignmentRepo.getAssignmentById(assignmentId = assignmentId)
        assert(assignment == null)
    }

    @Test
    fun `can update a assignment title`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val assignmentRepo = JdbiAssignmentRepository(handle = handle)
        val userId = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        val courseId = courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = userId))
        val classroomId = classroomRepo.createClassroom(classroom = ClassroomInput(name = "Classroom 1", courseId = courseId, inviteLink = "linking link"))
        val assignmentId = assignmentRepo.createAssignment(
            assignment = AssignmentInput(
                classroomId = classroomId,
                maxNumberElems = 10,
                maxNumberGroups = 10,
                releaseDate = Timestamp.from(Instant.now()),
                description = "aaaa",
                title = "aaa",
            ),
        )
        val newTitle = "title changed"
        assignmentRepo.updateAssignmentTitle(assignmentId = assignmentId, title = newTitle)
        val assignment = assignmentRepo.getAssignmentById(assignmentId = assignmentId)
        assert(assignment?.title == newTitle)
    }

    @Test
    fun `can update a assignment description`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val assignmentRepo = JdbiAssignmentRepository(handle = handle)
        val userId = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        val courseId = courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = userId))
        val classroomId = classroomRepo.createClassroom(classroom = ClassroomInput(name = "Classroom 1", courseId = courseId, inviteLink = "linking link"))
        val assignmentId = assignmentRepo.createAssignment(
            assignment = AssignmentInput(
                classroomId = classroomId,
                maxNumberElems = 10,
                maxNumberGroups = 10,
                releaseDate = Timestamp.from(Instant.now()),
                description = "aaaa",
                title = "aaa",
            ),
        )
        val newDescription = "description changed"
        assignmentRepo.updateAssignmentDescription(assignmentId = assignmentId, description = newDescription)
        val assignment = assignmentRepo.getAssignmentById(assignmentId = assignmentId)
        assert(assignment?.description == newDescription)
    }

    @Test
    fun `can update a assignment max groups`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val assignmentRepo = JdbiAssignmentRepository(handle = handle)
        val userId = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        val courseId = courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = userId))
        val classroomId = classroomRepo.createClassroom(classroom = ClassroomInput(name = "Classroom 1", courseId = courseId, inviteLink = "linking link"))
        val assignmentId = assignmentRepo.createAssignment(
            assignment = AssignmentInput(
                classroomId = classroomId,
                maxNumberElems = 10,
                maxNumberGroups = 10,
                releaseDate = Timestamp.from(Instant.now()),
                description = "aaaa",
                title = "aaa",
            ),
        )
        val newGroupsMax = 12
        assignmentRepo.updateAssignmentNumbGroups(assignmentId = assignmentId, numb = newGroupsMax)
        val assignment = assignmentRepo.getAssignmentById(assignmentId = assignmentId)
        assert(assignment?.maxNumberGroups == newGroupsMax)
    }
    @Test
    fun `can update a assignment max elems`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val assignmentRepo = JdbiAssignmentRepository(handle = handle)
        val userId = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        val courseId = courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = userId))
        val classroomId = classroomRepo.createClassroom(classroom = ClassroomInput(name = "Classroom 1", courseId = courseId, inviteLink = "linking link"))
        val assignmentId = assignmentRepo.createAssignment(
            assignment = AssignmentInput(
                classroomId = classroomId,
                maxNumberElems = 10,
                maxNumberGroups = 10,
                releaseDate = Timestamp.from(Instant.now()),
                description = "aaaa",
                title = "aaa",
            ),
        )
        val newElementsMax = 3
        assignmentRepo.updateAssignmentNumbElemsPerGroup(assignmentId = assignmentId, numb = newElementsMax)
        val assignment = assignmentRepo.getAssignmentById(assignmentId = assignmentId)
        assert(assignment?.maxNumberElems == newElementsMax)
    }
}
