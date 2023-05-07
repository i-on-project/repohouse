package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiUsersRepository
import com.isel.leic.ps.ion_classcode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class UsersRepositoryTests {

    @Test
    fun `can check if email is already in use`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val email = "test@alunos.isel.pt"
        val res = userRepo.checkIfEmailExists(email = email)
        assert(res)
    }

    @Test
    fun `can check if email is not already in use`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val email = "fail@alunos.isel.pt"
        val res = userRepo.checkIfEmailExists(email = email)
        assert(!res)
    }

    @Test
    fun `can check if github id is already in use`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val githubId = 12345L
        val res = userRepo.checkIfGithubIdExists(githubId = githubId)
        assert(res)
    }

    @Test
    fun `can check if github id is not already in use`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val githubId = 445566L
        val res = userRepo.checkIfGithubIdExists(githubId = githubId)
        assert(!res)
    }

    @Test
    fun `can check if github username is already in use`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val githubUsername = "test123"
        val res = userRepo.checkIfGithubUsernameExists(githubUsername = githubUsername)
        assert(res)
    }

    @Test
    fun `can check if github username is not already in use`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val githubUsername = "fail"
        val res = userRepo.checkIfGithubUsernameExists(githubUsername = githubUsername)
        assert(!res)
    }

    @Test
    fun `can check if token is already in use`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val token = "token"
        val res = userRepo.checkIfTokenExists(token = token)
        assert(res)
    }

    @Test
    fun `can check if token is not already in use`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val token = "fail"
        val res = userRepo.checkIfTokenExists(token = token)
        assert(!res)
    }

    @Test
    fun `can check if github token is already in use`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val githubToken = "token"
        val res = userRepo.checkIfGithubTokenExists(githubToken = githubToken)
        assert(res)
    }

    @Test
    fun `can check if github token is not already in use`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val githubToken = "fail"
        val res = userRepo.checkIfGithubTokenExists(githubToken = githubToken)
        assert(!res)
    }

    @Test
    fun `can check if schoolId is already in use`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val schoolId = 1234
        val res = userRepo.checkIfSchoolIdExists(schoolId = schoolId)
        assert(res)
    }

    @Test
    fun `can check if schoolId is not already in use`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val schoolId = 48309
        val res = userRepo.checkIfSchoolIdExists(schoolId = schoolId)
        assert(!res)
    }

    @Test
    fun `can create a pending student`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        userRepo.createPendingStudent(student = StudentInput(name = "test1245", email = "test5@alunos.isel.pt", githubUsername = "test1a23", token = "token5", githubId = 124345))
    }

    @Test
    fun `can get a pending user`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val pendingUser = userRepo.getPendingStudentByGithubId(githubId = 2222)
        assert(pendingUser != null && pendingUser.email == "test2@alunos.isel.pt")
    }

    @Test
    fun `can not get a pending user`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val pendingUser = userRepo.getPendingStudentByGithubId(githubId = 1234)
        assert(pendingUser == null )
    }

    @Test
    fun `can get a pending teacher`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val pendingUser = userRepo.getPendingTeacherByGithubId(githubId = 2227)
        assert(pendingUser != null && pendingUser.email == "test4@alunos.isel.pt")
    }

    @Test
    fun `can not get a pending teacher`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val pendingUser = userRepo.getPendingTeacherByGithubId(githubId = 1234)
        assert(pendingUser == null)
    }

    @Test
    fun `can delete pending users`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        userRepo.deletePendingUsers()
        val pendingStudent = userRepo.getPendingStudentByGithubId(githubId = 2225)
        val pendingTeacher = userRepo.getPendingTeacherByGithubId(githubId = 2227)
        assert(pendingStudent == null && pendingTeacher == null)
    }

    @Test
    fun `can create a student`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        userRepo.createStudent(student = StudentInput(name = "test1245", email = "test8@alunos.isel.pt", githubUsername = "test1a23", token = "token8", githubId = 124345))
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
    fun `can create a pending teacher`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        userRepo.createPendingTeacher(teacher = TeacherInput(name = "test142", email = "test5@alunos.isel.pt", githubUsername = "test1239", githubToken = "token5", githubId = 123415, token = "token5"))
    }

    @Test
    fun `can create a teacher`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        userRepo.createTeacher(teacher = TeacherInput(name = "test142", email = "test8@alunos.isel.pt", githubUsername = "test1239", githubToken = "token142", githubId = 123415, token = "token142"))
    }

    @Test
    fun `can get all students`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val list = userRepo.getAllStudents()
        assert(list.size == 4)
    }

    @Test
    fun `can get a student`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val id = 3
        val student = userRepo.getUserById(userId = id) ?: fail("Student not found")
        assert(student is Student)
    }

    @Test
    fun `can get a teacher`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val id = 1
        val teacher = userRepo.getUserById(userId = id) ?: fail("Teacher not found")
        assert(teacher is Teacher)
    }

    @Test
    fun `can can update user status`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val id = 5
        userRepo.updateUserStatus(id = id)
        val user = userRepo.getUserById(userId = id) ?: fail("User not found")
        assert(user.isCreated)
    }

    @Test
    fun `can get all teachers`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val list = userRepo.getAllTeachers()
        assert(list.size == 4)
    }

    @Test
    fun `can get a user name`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val student = userRepo.getUserById(userId = 3) ?: fail("Student not found")
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
    fun `can get a user by token`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val token = "token2"
        val user = userRepo.getUserByToken(token = token) ?: fail("User not found")
        assert(user.name == "student1")
    }

    @Test
    fun `can get github token of a teacher`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val token = userRepo.getTeacherGithubToken(id = 1) ?: fail("User not found")
        assert(token == "token")
    }

    @Test
    fun `can not get github token of a teacher`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val token = userRepo.getTeacherGithubToken(id = 3)
        assert(token == null)
    }

    @Test
    fun `update teacher github token`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        userRepo.updateTeacherGithubToken(id = 1, token = "test")
        val token = userRepo.getTeacherGithubToken(id = 1)
        assert(token == "test")
    }

    @Test
    fun `store teacher access token`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        userRepo.storeAccessTokenEncrypted(token = "token1234", githubId = 1234187)
        val token = userRepo.getAccessTokenEncrypted(githubId = 1234187)
        assert(token == "token1234")
    }

    @Test
    fun `get teacher access token`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val token = userRepo.getAccessTokenEncrypted(githubId = 12345)
        assert(token == "accessstoken1")
    }

    @Test
    fun `can not get teacher access token`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        val token = userRepo.getAccessTokenEncrypted(githubId = 99999)
        assert(token == null)
    }

    @Test
    fun `delete teacher access token`() = testWithHandleAndRollback { handle ->
        val userRepo = JdbiUsersRepository(handle = handle)
        userRepo.deleteAccessTokenEncrypted(githubId = 12345)
        val token = userRepo.getAccessTokenEncrypted(githubId = 12345)
        assert(token == null)
    }
}
