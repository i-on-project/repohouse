package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiCourseRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiUsersRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test

class CourseRepositoryTests {
    @Test
    fun `can create a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val id = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = id))
        courseRepo.createCourse(course = CourseInput(orgUrl = "https://pdm.isel.pt", name = "PDM", teacherId = id))
    }

    @Test
    fun `can get a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val id = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = id))
        val course = courseRepo.getCourse(teacherId = id)
        assert(course != null)
    }

    @Test
    fun `can delete a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val teacherId = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        val courseId = courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = teacherId))
        courseRepo.deleteCourse(courseId = courseId)
        val course = courseRepo.getCourse(teacherId = teacherId)
        assert(course == null)
    }

    @Test
    fun `can put a student in a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val teacherId = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        val studentId = usersRepo.createStudent(student = StudentInput(name = "test14", email = "test2@alunos.isel.pt", githubUsername = "test12345", schoolId = 12345, githubId = 123456, token = "token2"))
        val studentId1 = usersRepo.createStudent(student = StudentInput(name = "test13", email = "test1@alunos.isel.pt", githubUsername = "test1234", schoolId = 12346, githubId = 1345, token = "token1"))
        usersRepo.createStudent(student = StudentInput(name = "test14", email = "test3@alunos.isel.pt", githubUsername = "test122", schoolId = 125446, githubId = 132345, token = "token3"))
        val courseId = courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = teacherId))
        courseRepo.enterCourse(courseId = courseId, studentId = studentId)
        courseRepo.enterCourse(courseId = courseId, studentId = studentId1)
        val studentsInCourse = courseRepo.getStudentInCourse(courseId = courseId)
        assert(studentsInCourse.size == 2)
    }

    @Test
    fun `can delete a student from a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val usersRepo = JdbiUsersRepository(handle = handle)
        val teacherId = usersRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        val studentId = usersRepo.createStudent(student = StudentInput(name = "test14", email = "test2@alunos.isel.pt", githubUsername = "test12345", schoolId = 12345, githubId = 123456, token = "token2"))
        val studentId1 = usersRepo.createStudent(student = StudentInput(name = "test13", email = "test1@alunos.isel.pt", githubUsername = "test1234", schoolId = 12346, githubId = 1345, token = "token1"))
        val courseId = courseRepo.createCourse(course = CourseInput(orgUrl = "https://daw.isel.pt", name = "DAW", teacherId = teacherId))
        courseRepo.enterCourse(courseId = courseId, studentId = studentId)
        courseRepo.enterCourse(courseId = courseId, studentId = studentId1)
        courseRepo.leaveCourse(courseId = courseId, studentId = studentId1)
        val studentsInCourse = courseRepo.getStudentInCourse(courseId = courseId)
        assert(studentsInCourse.size == 1)
    }
}
