package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiClassroomRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiCourseRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiUsersRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test

class ClassroomRepositoryTests {
    @Test
    fun `can create classroom`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val id = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        val courseId = courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = id))
        classroomRepo.createClassroom(classroom = ClassroomInput(name = "Classroom 1", courseId = courseId, inviteLink = "linking link"))
    }

    @Test
    fun `can get the classroom by id`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val id = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        val courseId = courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = id))
        val classroomId = classroomRepo.createClassroom(classroom = ClassroomInput(name = "Classroom 1", courseId = courseId, inviteLink = "linking link"))
        val classroom = classroomRepo.getClassroomById(classroomId = classroomId)
        assert(classroom != null)
    }

    @Test
    fun `can delete classroom`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val id = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        val courseId = courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = id))
        val classroomId = classroomRepo.createClassroom(classroom = ClassroomInput(name = "Classroom 1", courseId = courseId, inviteLink = "linking link"))
        classroomRepo.deleteClassroom(classroomId = classroomId)
    }

    @Test
    fun `can get the classroom assignments`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val classroomRepo = JdbiClassroomRepository(handle = handle)
        val id = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        val courseId = courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = id))
        val classroomId = classroomRepo.createClassroom(classroom = ClassroomInput(name = "Classroom 1", courseId = courseId, inviteLink = "linking link"))
        val classroom = classroomRepo.getClassroomById(classroomId = classroomId)
        assert(classroom != null)
    }
}
