package com.isel.leic.ps.ionClassCode.services

import com.isel.leic.ps.ionClassCode.domain.Course
import com.isel.leic.ps.ionClassCode.domain.Teacher
import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ionClassCode.http.model.problem.Problem
import com.isel.leic.ps.ionClassCode.repository.transaction.TransactionManager
import com.isel.leic.ps.ionClassCode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias UserAuthenticationResult = Result<UserServicesError, User>
typealias StoreChallengeInfoResult = Result<UserServicesError, Unit>
typealias UserByGithubIdResult = Result<UserServicesError, User>
typealias UserCoursesResponse = Result<UserServicesError, List<Course>>
typealias VerifySecretResult = Result<UserServicesError, Unit>

/**
 * Error codes for the services
 */
sealed class UserServicesError {
    object UserNotFound : UserServicesError()
    object UserNotAuthenticated : UserServicesError()
    object InvalidToken : UserServicesError()
    object InternalError : UserServicesError()
    object NotTeacher : UserServicesError()
    object InvalidData : UserServicesError()
    object InvalidSecret : UserServicesError()
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
                return@run Result.Problem(UserServicesError.UserNotAuthenticated)
            } else {
                return@run Result.Success(user)
            }
        }
    }

    /**
     * Method to store the access token encrypted
     */
    fun storeChallengeInfo(challengeMethod: String, challenge: String, state: String): StoreChallengeInfoResult {
        if (challengeMethod.isEmpty() || challenge.isEmpty()) return Result.Problem(UserServicesError.InvalidData)
        if (challengeMethod != "s256" && challengeMethod != "plain") return Result.Problem(UserServicesError.InvalidData)
        return transactionManager.run {
            it.usersRepository.storeChallengeInfo(challengeMethod = challengeMethod, challenge = challenge, state = state)
            return@run Result.Success(Unit)
        }
    }

    fun verifySecret(secret: String, state: String): VerifySecretResult {
        if (secret.isEmpty() || state.isEmpty()) return Result.Problem(value = UserServicesError.InvalidData)
        return transactionManager.run {
            return@run if (it.usersRepository.verifySecret(secret = secret, state = state)) {
                Result.Success(value = Unit)
            } else {
                Result.Problem(value = UserServicesError.InvalidSecret)
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
                return@run Result.Problem(UserServicesError.UserNotFound)
            } else {
                return@run Result.Success(user)
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
                return@run Result.Problem(UserServicesError.UserNotFound)
            } else {
                return@run Result.Success(user)
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
            val user = it.usersRepository.getUserById(userId) ?: return@run Result.Problem(UserServicesError.InternalError)
            val courses = if (user is Teacher) {
                it.courseRepository.getAllTeacherCourses(userId)
            } else {
                it.courseRepository.getAllStudentCourses(userId)
            }
            return@run Result.Success(courses)
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
            UserServicesError.InvalidData -> Problem.invalidInput
            UserServicesError.InvalidSecret -> Problem.unauthorized
        }
    }
}
