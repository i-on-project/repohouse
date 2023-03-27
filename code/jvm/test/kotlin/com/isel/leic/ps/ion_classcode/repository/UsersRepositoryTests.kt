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
        userRepo.createStudent(student = StudentInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", schoolId = 12345, token = "token", githubId = 12345))
        userRepo.createStudent(student = StudentInput(name = "test13", email = "test1@alunos.isel.pt", githubUsername = "test1234", schoolId = 12346, token = "token1", githubId = 1345))
    }

    @Test
    fun `can create a teacher`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        userRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        userRepo.createTeacher(teacher = TeacherInput(name = "test13", email = "test1@alunos.isel.pt", githubUsername = "test1123", githubToken = "token1", githubId = 12344, token = "token1"))
    }

    @Test
    fun `can create a student and retrieve it by name`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val id1 = userRepo.createStudent(student = StudentInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", schoolId = 12345, token = "token", githubId = 12345))
        userRepo.createStudent(student = StudentInput(name = "test13", email = "test1@alunos.isel.pt", githubUsername = "test1234", schoolId = 12346, token = "token1", githubId = 1345))
        val student = userRepo.getUserById(id = id1) ?: fail("Student not found")
        assert(student.name == "test12")
    }

    @Test
    fun `can create a user and retrieve it by email`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val email = "test@alunos.isel.pt"
        userRepo.createStudent(student = StudentInput(name = "test12", email = email, githubUsername = "test123", schoolId = 12345, token = "token", githubId = 12345))
        userRepo.createStudent(student = StudentInput(name = "test13", email = "test1@alunos.isel.pt", githubUsername = "test1234", schoolId = 12346, token = "token1", githubId = 1345))
        val student = userRepo.getUserByEmail(email = email) ?: fail("Student not found")
        assert(student.name == "test12")
    }

    @Test
    fun `can create a user and retrieve by token it`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val token = "token"
        userRepo.createStudent(student = StudentInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", schoolId = 12345, token = token, githubId = 12345))
        userRepo.createStudent(student = StudentInput(name = "test13", email = "test1@alunos.isel.pt", githubUsername = "test1234", schoolId = 12346, token = "token1", githubId = 1345))
        val user = userRepo.getUserByToken(token = token) ?: fail("User not found")
        assert(user.name == "test12")
    }

    @Test
    fun `can eliminate a student`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val id = userRepo.createStudent(student = StudentInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", schoolId = 12345, token = "token", githubId = 12345))
        userRepo.deleteStudent(id = id)
        val student = userRepo.getUserById(id = id)
        assert(student == null)
    }

    @Test
    fun `can eliminate a teacher`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val id = userRepo.createTeacher(teacher = TeacherInput(name = "test12", email = "test@alunos.isel.pt", githubUsername = "test123", githubToken = "token", githubId = 12345, token = "token"))
        userRepo.deleteTeacher(id = id)
        val teacher = userRepo.getUserById(id = id)
        assert(teacher == null)
    }
}
