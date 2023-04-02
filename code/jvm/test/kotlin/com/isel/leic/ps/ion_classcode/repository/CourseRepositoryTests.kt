package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiCourseRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class CourseRepositoryTests {
    @Test
    fun `can create a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        courseRepo.createCourse(course = CourseInput(orgUrl = "https://pdm.isel.pt", name = "PDM", teacherId = 2))
    }

    @Test
    fun `can get a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val courseId = 1
        val name = "DAW"
        val course = courseRepo.getCourse(courseId = courseId) ?: fail("Should be able to get a course")
        assert(course.name == name)
    }

    @Test
    fun `can delete a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val courseId = 3
        courseRepo.deleteCourse(courseId = courseId)
    }

    @Test
    fun `cannot delete a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val courseId = 1
        try {
            courseRepo.deleteCourse(courseId = courseId)
            fail("Should not be able to delete a course")
        } catch (e: Exception) {
            assert(true)
        }
    }

    @Test
    fun `can put a student in a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val courseId = 3
        courseRepo.enterCourse(courseId = courseId, studentId = 4)
        courseRepo.enterCourse(courseId = courseId, studentId = 5)
        val studentsInCourse = courseRepo.getStudentInCourse(courseId = courseId)
        assert(studentsInCourse.size == 2)
    }

    @Test
    fun `can delete a student from a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val courseId = 3
        courseRepo.enterCourse(courseId = courseId, studentId = 4)
        courseRepo.enterCourse(courseId = courseId, studentId = 5)
        courseRepo.leaveCourse(courseId = courseId, studentId = 4)
        val studentsInCourse = courseRepo.getStudentInCourse(courseId = courseId)
        assert(studentsInCourse.size == 1)
    }

    @Test
    fun `can get a teacher courses`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val teacherId = 2
        val courses = courseRepo.getAllTeacherCourses(teacherId = teacherId)
        assert(courses.size == 2)
    }

    @Test
    fun `can get a student courses`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val studentId = 3
        val courses = courseRepo.getAllStudentCourses(studentId = studentId)
        assert(courses.size == 1)
    }

    @Test
    fun `can get students in a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val courseId = 1
        val students = courseRepo.getStudentInCourse(courseId = courseId)
        assert(students.size == 2)
    }
}
