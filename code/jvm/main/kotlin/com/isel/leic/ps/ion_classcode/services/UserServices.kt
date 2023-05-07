package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.Tokens
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias UserAuthenticationResult = Result<UserServicesError, User>
typealias StoreAccessTokenResult = Result<UserServicesError, Unit>
typealias UserByGithubIdResult = Result<UserServicesError, User>
typealias UserCoursesResponse = Result<UserServicesError, List<Course>>
typealias GetAccessTokenResult = Result<UserServicesError, Tokens>

/**
 * Error codes for the services
 */
sealed class UserServicesError {
    object UserNotFound : UserServicesError()
    object UserNotAuthenticated : UserServicesError()
    object InvalidToken : UserServicesError()
    object InternalError : UserServicesError()
    object NotTeacher : UserServicesError()
}

/**
 * Service to the user services
 */
@Component
class UserServices(
    private val transactionManager: TransactionManager,
) {

    /**
     * Method to check the token from a user
     */
    fun checkAuthentication(token: String): UserAuthenticationResult {
        if (token.isEmpty()) return Result.Problem(UserServicesError.InvalidToken)
        return transactionManager.run {
            val user = it.usersRepository.getUserByToken(token)
            if (user == null) {
                Result.Problem(UserServicesError.UserNotAuthenticated)
            } else {
                Result.Success(user)
            }
        }
    }

    /**
     * Method to store the access token encrypted
     */
    fun storeAccessTokenEncrypted(token: String, githubId: Long): StoreAccessTokenResult {
        if (token.isEmpty()) return Result.Problem(UserServicesError.InvalidToken)
        return transactionManager.run {
            val user = it.usersRepository.getUserByGithubId(githubId)
            if (user !is Teacher) return@run Result.Problem(UserServicesError.InternalError)
            it.usersRepository.storeAccessTokenEncrypted(token, githubId)
            Result.Success(Unit)
        }
    }

    /**
     * Method to get the access token encrypted and the classcode user token
     */

    fun getTokens(githubId: Long): GetAccessTokenResult {
        return transactionManager.run {
            val user = it.usersRepository.getUserByGithubId(githubId) ?: return@run Result.Problem(UserServicesError.UserNotFound)
            if (user !is Teacher) return@run Result.Problem(UserServicesError.NotTeacher)
            val accessToken = it.usersRepository.getAccessTokenEncrypted(githubId)
            if (accessToken == null) {
                Result.Problem(UserServicesError.InternalError)
            } else {
                it.usersRepository.deleteAccessTokenEncrypted(githubId)
                Result.Success(Tokens(accessToken, user.token))
            }
        }
    }

    /**
     * Method to get a user by GitHub id
     */
    fun getUserByGithubId(githubId: Long): UserByGithubIdResult {
        return transactionManager.run {
            val user = it.usersRepository.getUserByGithubId(githubId)
            if (user == null) {
                Result.Problem(UserServicesError.UserNotFound)
            } else {
                Result.Success(user)
            }
        }
    }

    /**
     * Method to get a pending user by GitHub id
     */
    fun getPendingUserByGithubId(githubId: Long, position: String): UserByGithubIdResult {
        return transactionManager.run {
            val user = if (position == "Teacher") it.usersRepository.getPendingTeacherByGithubId(githubId) else it.usersRepository.getPendingStudentByGithubId(githubId)
            if (user == null) {
                Result.Problem(UserServicesError.UserNotFound)
            } else {
                Result.Success(user)
            }
        }
    }

    /**
     * Method to delete pending users from the database after each day at 12:00 pm
     */

    @Scheduled(cron = "0 0 0 * * *")
    fun deletePendingUsers() {
        return transactionManager.run {
            it.usersRepository.deletePendingUsers()
        }
    }

    /**
     * Method to get all the courses of a user
     */
    fun getAllUserCourses(userId: Int): UserCoursesResponse {
        return transactionManager.run {
            it.usersRepository.getUserById(userId) ?: Result.Problem(UserServicesError.InternalError)
            val courses = it.courseRepository.getAllUserCourses(userId)
            Result.Success(courses)
        }
    }

    /**
     * Function to handle errors about the user.
     */
    fun problem(error: UserServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            UserServicesError.UserNotFound -> Problem.notFound
            UserServicesError.UserNotAuthenticated -> Problem.unauthenticated
            UserServicesError.InvalidToken -> Problem.unauthenticated
            UserServicesError.NotTeacher -> Problem.notTeacher
            UserServicesError.InternalError -> Problem.internalError
        }
    }
}
