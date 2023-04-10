package com.isel.leic.ps.ion_classcode

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.repository.UsersRepository
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

val mockedUsersRepository = mock<UsersRepository> {
    on {
        createStudent(student = StudentInput(name = "test1245", email = "test@alunos.isel.pt", githubUsername = "test1a23", token = "token5", githubId = 124345))
    } doReturn Student(name = "test1245", email = "test@alunos.isel.pt", githubUsername = "test1a23", token = "token5", githubId = 124345, isCreated = false, id = 3, schoolId = null)

    on {
        updateStudentSchoolId(userId = 5, schoolId = 1256)
    } doAnswer {}

    on {
        createTeacher(
            teacher = TeacherInput(name = "test142", email = "test@alunos.isel.pt", githubUsername = "test1239", githubToken = "token5", githubId = 123415, token = "token5"),
        )
    } doReturn Teacher(name = "test142", id = 2, email = "test@alunos.isel.pt", githubUsername = "test1239", githubId = 123415, token = "token5", isCreated = false)

    on {
        getStudent(studentId = 4)
    } doReturn Student(name = "student2", token = "token3", githubId = 1234152, githubUsername = "test12345", isCreated = false, email = "test3@alunos.isel.pt", id = 4, schoolId = 1235)

    on {
        getTeacher(teacherId = 2)
    } doReturn Teacher(name = "teacher2", isCreated = false, githubUsername = "test1234", githubId = 123452, token = "token1", id = 2, email = "test1@alunos.isel.pt")

    on {
        getUserById(id = 4)
    } doReturn Student(name = "student2", token = "token3", githubId = 1234152, githubUsername = "test12345", isCreated = false, email = "test3@alunos.isel.pt", id = 4, schoolId = 1235)

    on {
        getUserByGithubId(githubId = 1234)
    } doReturn Teacher(name = "teacher2", isCreated = false, githubUsername = "test1234", githubId = 1234, token = "token1", id = 2, email = "test1@alunos.isel.pt")
    on {
        getUserByToken(token = "bearer")
    } doReturn Teacher(name = "teacher2", isCreated = false, githubUsername = "test1234", githubId = 1234, token = "token1", id = 2, email = "test1@alunos.isel.pt")

    on {
        checkIfEmailExists(email = "test5@alunos.isel.pt")
    } doReturn true

    on {
        checkIfGithubIdExists(githubId = 1234)
    } doReturn true

    on {
        checkIfGithubTokenExists(githubToken = "token")
    } doReturn true

    on {
        checkIfGithubUsernameExists(githubUsername = "test142")
    } doReturn true

    on {
        checkIfTokenExists(token = "token")
    } doReturn true

    on {
        checkIfSchoolIdExists(schoolId = 1235)
    } doReturn true
}