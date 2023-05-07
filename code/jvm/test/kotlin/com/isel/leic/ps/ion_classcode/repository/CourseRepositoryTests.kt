package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiCourseRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class CourseRepositoryTests {

    @Test
    fun `can check if a course name already exists`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val name = "DAW"
        val res = courseRepo.checkIfCourseNameExists(name = name)
        assert(res)
    }

    @Test
    fun `can check if a course name does not exist`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val name = "LSD"
        val res = courseRepo.checkIfCourseNameExists(name = name)
        assert(!res)
    }

    @Test
    fun `can check if a course org url already exists`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val orgUrl = "https://daw.isel.pt"
        val res = courseRepo.checkIfOrgUrlExists(orgUrl = orgUrl)
        assert(res)
    }

    @Test
    fun `can check if a course org url does not exist`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val orgUrl = "https://lsd.isel.pt"
        val res = courseRepo.checkIfOrgUrlExists(orgUrl = orgUrl)
        assert(!res)
    }

    @Test
    fun `can create a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val created = courseRepo.createCourse(course = CourseInput(orgUrl = "https://pdm.isel.pt", name = "LAE", teacherId = 2, orgId = 123456))
        val course = courseRepo.getCourse(courseId = created.id)
        assert(course != null)
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
    fun `cannot get a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val courseId = 10
        val course = courseRepo.getCourse(courseId = courseId)
        assert(course == null)
    }

    @Test
    fun `can get a course classrooms`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val courseId = 1
        val course = courseRepo.getCourseAllClassrooms(courseId = courseId)
        assert(course.size == 2)
    }

    @Test
    fun `can delete a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val courseId = 3
        courseRepo.deleteCourse(courseId = courseId)
        val course = courseRepo.getCourse(courseId = courseId)
        assert(course == null)
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
    fun `can get a teacher courses`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val userId = 2
        val courses = courseRepo.getAllUserCourses(userId = userId)
        assert(courses.size == 2)
    }

    @Test
    fun `can add a user to a courses`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val teacherId = 6
        val courseId = 2
        courseRepo.addTeacherToCourse(teacherId = teacherId, courseId = courseId)
        val courses = courseRepo.getAllUserCourses(userId = teacherId)
        assert(courses.size == 2)
    }

    @Test
    fun `can get students in a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val courseId = 1
        val students = courseRepo.getStudentInCourse(courseId = courseId)
        assert(students.size == 2)
    }

    @Test
    fun `can get teachers in a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val courseId = 1
        val students = courseRepo.getCourseTeachers(courseId = courseId)
        assert(students.size == 3)
    }

    @Test
    fun `can get course by orgUrl`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val orgUrl = "https://daw.isel.pt"
        val course = courseRepo.getCourseByOrg(orgUrl = orgUrl) ?: fail("Should be able to get a course")
        assert(course.id == 1)
    }

    @Test
    fun `can get course by name`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val name = "DAW"
        val course = courseRepo.getCourseByName(name = name) ?: fail("Should be able to get a course")
        assert(course.id == 1)
    }

    @Test
    fun `can verify if a user is in a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val userId = 3
        val courseId = 1
        val isIn = courseRepo.isStudentInCourse(studentId = userId, courseId = courseId)
        assert(isIn)
    }

    @Test
    fun `can verify if a user is not in a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val userId = 3
        val courseId = 2
        val isIn = courseRepo.isStudentInCourse(studentId = userId, courseId = courseId)
        assert(isIn)
    }

    @Test
    fun `can archive a course`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val courseId = 2
        courseRepo.archiveCourse(courseId = courseId)
        val course = courseRepo.getCourse(courseId = courseId) ?: fail("Should be able to get a course")
        assert(course.isArchived)
    }

    @Test
    fun `get student course classrooms`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val courseId = 1
        val classrooms = courseRepo.getCourseUserClassrooms(courseId = courseId, userId = 3, true)
        assert(classrooms.size == 1)
    }

    @Test
    fun `get teacher course classrooms`() = testWithHandleAndRollback { handle ->
        val courseRepo = JdbiCourseRepository(handle = handle)
        val courseId = 1
        val classrooms = courseRepo.getCourseUserClassrooms(courseId = courseId, userId = 1, false)
        assert(classrooms.size == 1)
    }
}
