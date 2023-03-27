package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.http.controllers.AuthController
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubUserInfo
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiUsersRepository
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import java.text.FieldPosition
import org.springframework.stereotype.Component

typealias UserAuthenticationResult = Either<UserServicesError, User>
typealias UserByIdResult = Either<UserServicesError, User>
typealias UserByGithubIdResult = Either<UserServicesError, User>

sealed class UserServicesError {
    object UserNotFound : UserServicesError()
    object UserNotTeacher : UserServicesError()
    object UserNotStudent : UserServicesError()
    object UserNotAuthenticated : UserServicesError()
    object UserNotAuthorized : UserServicesError()
    object InvalidGithubId : UserServicesError()
    object InvalidData : UserServicesError()
}

@Component
class UserServices(
    private val transactionManager: TransactionManager,
    private val userRepo: JdbiUsersRepository,
) {
    fun checkAuthentication(bearerToken: String): UserAuthenticationResult {
        if (bearerToken.isEmpty()) return Either.Left(UserServicesError.InvalidData)
        return transactionManager.run {
            val user = userRepo.getUserByToken(bearerToken)
            if (user == null) Either.Left(UserServicesError.UserNotFound)
            else Either.Right(user)
        }
    }

    fun createUser(user: GitHubUserInfo,position: String): UserByIdResult {
        if(position != "Student" && position != "Teacher") return Either.Left(UserServicesError.InvalidData)
        if (user.name.isEmpty()) return Either.Left(UserServicesError.InvalidData)
        return transactionManager.run {
            TODO()
        }
    }

    fun getUserByGithubId(githubId: Long): UserByGithubIdResult {
        if (githubId <= 0) return Either.Left(UserServicesError.InvalidGithubId)
        return transactionManager.run {
            val user = userRepo.getUserByGithubId(githubId)
            if (user == null) Either.Left(UserServicesError.UserNotFound)
            else Either.Right(user)
        }
    }

}
