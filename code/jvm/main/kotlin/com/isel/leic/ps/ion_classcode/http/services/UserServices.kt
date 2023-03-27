package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.controllers.AuthController
import com.isel.leic.ps.ion_classcode.repository.jdbi.JdbiUsersRepository
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias UserByGithubIdResult = Either<UserServicesError, User>

sealed class UserServicesError {
    object UserNotFound : UserServicesError()
    object UserNotTeacher : UserServicesError()
    object UserNotStudent : UserServicesError()
    object UserNotAuthenticated : UserServicesError()
    object UserNotAuthorized : UserServicesError()
    object InvalidGithubId : UserServicesError()
}

@Component
class UserServices(
    private val userRepo: JdbiUsersRepository,
) {
    fun checkAuthenticationDummy(bearerToken: String): User {
        // Dummy implementation
        return Teacher("teacher", "teach@gamil.com", 12, "teacher", "teach", true)
    }

    fun getUserByGithubId(githubId: Long): UserByGithubIdResult {
        if (githubId <= 0) return Either.Left(UserServicesError.InvalidGithubId)
        val user = userRepo.getUserByGithubId(githubId)
        return if (user == null) Either.Left(UserServicesError.UserNotFound)
        else Either.Right(user)

}
