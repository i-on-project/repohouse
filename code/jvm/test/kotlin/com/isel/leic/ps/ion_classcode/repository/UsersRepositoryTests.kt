package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiUsersRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class UsersRepositoryTests {
    @Test
    fun `can create a student`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        userRepo.createStudent(student = StudentInput(name = "test1245", email = "test5@alunos.isel.pt", githubUsername = "test1a23", token = "token5", githubId = 124345))
    }

    @Test
    fun `can update the schoolId`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val userId = 5
        val schoolId = 1256
        userRepo.updateStudentSchoolId(userId = userId, schoolId = schoolId)
        val id = userRepo.getStudentSchoolId(id = userId)
        assert(id == schoolId)
    }

    @Test
    fun `can create a teacher`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        userRepo.createTeacher(teacher = TeacherInput(name = "test142", email = "test5@alunos.isel.pt", githubUsername = "test1239", githubToken = "token5", githubId = 123415, token = "token5"))
    }

    @Test
    fun `can get all students`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val list = userRepo.getAllStudents()
        assert(list.size == 3)
    }

    @Test
    fun `can can update user status`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val id = 5
        userRepo.updateUserStatus(id = id)
        val user = userRepo.getUserById(id = id) ?: fail("User not found")
        assert(user.isCreated)
    }

    @Test
    fun `can get all teachers`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val list = userRepo.getAllTeachers()
        assert(list.size == 3)
    }

    @Test
    fun `can get a user name`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val student = userRepo.getUserById(id = 3) ?: fail("Student not found")
        assert(student.name == "student1")
    }

    @Test
    fun `can get a user by email`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val email = "test2@alunos.isel.pt"
        val student = userRepo.getUserByEmail(email = email) ?: fail("Student not found")
        assert(student.name == "student1")
    }

    @Test
    fun `can get a user githubId`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val githubId = 123425L
        val student = userRepo.getUserByGithubId(githubId = githubId) ?: fail("Student not found")
        assert(student.name == "student1")
    }

    @Test
    fun `can get a student school id`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val id = 3
        val schoolId = userRepo.getStudentSchoolId(id = id) ?: fail("Student not found")
        assert(schoolId == 1234)
    }

    @Test
    fun `can create a user and retrieve by token it`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val token = "token2"
        val user = userRepo.getUserByToken(token = token) ?: fail("User not found")
        assert(user.name == "student1")
    }

    @Test
    fun `can eliminate a student`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val id = 7
        userRepo.deleteStudent(id = id)
        val student = userRepo.getUserById(id = id)
        assert(student == null)
    }

    @Test
    fun `cannot eliminate a student`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val id = 3
        try {
            userRepo.deleteStudent(id = id)
            fail("Should not be able to delete a student")
        } catch (e: Exception) {
            assert(true)
        }
    }

    @Test
    fun `can eliminate a teacher`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val id = 7
        userRepo.deleteTeacher(id = id)
        val teacher = userRepo.getUserById(id = id)
        assert(teacher == null)
    }

    @Test
    fun `cannot eliminate a teacher`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val id = 1
        try {
            userRepo.deleteTeacher(id = id)
            fail("Should not be able to delete a student")
        } catch (e: Exception) {
            assert(true)
        }
    }
}
