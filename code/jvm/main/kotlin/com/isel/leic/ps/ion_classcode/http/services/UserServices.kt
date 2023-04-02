package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.http.controllers.AuthController
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubUserInfo
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiUsersRepository
import com.isel.leic.ps.ion_classcode.repository.jdbi.transaction.JdbiTransaction
import com.isel.leic.ps.ion_classcode.repository.jdbi.transaction.JdbiTransactionManager
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import java.text.FieldPosition
import org.springframework.stereotype.Component

typealias UserAuthenticationResult = Either<UserServicesError, User>
typealias UserByIdResult = Either<UserServicesError, User>
typealias UserByGithubIdResult = Either<UserServicesError, User>
typealias TeacherCreationResult = Either<UserServicesError, Teacher>
typealias StudentCreationResult = Either<UserServicesError, Student>

sealed class UserServicesError {
    object UserNotFound : UserServicesError()
    object UserNotTeacher : UserServicesError()
    object UserNotStudent : UserServicesError()
    object UserNotAuthenticated : UserServicesError()
    object UserNotAuthorized : UserServicesError()
    object InvalidGithubId : UserServicesError()
    object InvalidData : UserServicesError()
    object ErrorCreatingUser : UserServicesError()
}

@Component
class UserServices(
    private val transactionManager: TransactionManager,
) {
    fun checkAuthentication(bearerToken: String): UserAuthenticationResult {
        if (bearerToken.isEmpty()) return Either.Left(UserServicesError.InvalidData)
        return transactionManager.run {it ->
            val user = it.usersRepository.getUserByToken(bearerToken)
            if (user == null) Either.Left(UserServicesError.UserNotFound)
            else Either.Right(user)
        }
    }

    fun getUserById(userId: Int): UserByIdResult {
        if (userId <= 0) return Either.Left(UserServicesError.InvalidData)
        return transactionManager.run {
            val user = it.usersRepository.getUserById(userId)
            if (user == null) Either.Left(UserServicesError.UserNotFound)
            else Either.Right(user)
        }
    }

    fun createTeacher(teacher: TeacherInput): TeacherCreationResult {
        if (teacher.name.isEmpty() || teacher.email.isEmpty()) return Either.Left(UserServicesError.InvalidData)
        return transactionManager.run {
            val teacherId = it.usersRepository.createTeacher(teacher)
            if (teacherId <= 0) Either.Left(UserServicesError.ErrorCreatingUser)
            else Either.Right(it.usersRepository.getUserById(teacherId) as Teacher)
        }
    }

    fun createStudent(student: StudentInput): StudentCreationResult {
        if (student.name.isEmpty() || student.email.isEmpty()) return Either.Left(UserServicesError.InvalidData)
        return transactionManager.run {
            val studentId = it.usersRepository.createStudent(student)
            if (studentId <= 0) Either.Left(UserServicesError.ErrorCreatingUser)
            else Either.Right(it.usersRepository.getUserById(studentId) as Student)
        }
    }

    fun getUserByGithubId(githubId: Long): UserByGithubIdResult {
        if (githubId <= 0) return Either.Left(UserServicesError.InvalidGithubId)
        return transactionManager.run {
            val user = it.usersRepository.getUserByGithubId(githubId)
            if (user == null) Either.Left(UserServicesError.UserNotFound)
            else Either.Right(user)
        }
    }

}
