package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.tokenHash.TokenHash
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias UserAuthenticationResult = Either<UserServicesError, User>
typealias UserByIdResult = Either<UserServicesError, User>
typealias UserByGithubIdResult = Either<UserServicesError, User>
typealias TeacherCreationResult = Either<UserServicesError, Teacher>
typealias StudentCreationResult = Either<UserServicesError, Student>

/**
 * Error codes for the services
 */
sealed class UserServicesError {
    object UserNotFound : UserServicesError()
    object UserNotAuthenticated : UserServicesError()
    object InvalidGithubId : UserServicesError()
    object InvalidData : UserServicesError()
    object ErrorCreatingUser : UserServicesError()
    object InvalidToken : UserServicesError()
    object GithubUserNameInUse : UserServicesError()
    object EmailInUse : UserServicesError()
    object GithubIdInUse : UserServicesError()
    object TokenInUse : UserServicesError()
    object GithubTokenInUse : UserServicesError()
    object SchoolIdInUse : UserServicesError()
}

/**
 * Service to the user services
 */
@Component
class UserServices(
    private val transactionManager: TransactionManager,
    private val tokenHash: TokenHash,
) {
    /**
     * Method to check the token from a user
     */
    fun checkAuthentication(token: String): UserAuthenticationResult {
        if (token.isEmpty()) return Either.Left(value = UserServicesError.InvalidToken)
        val hash = tokenHash.getTokenHash(token)
        return transactionManager.run {
            val user = it.usersRepository.getUserByToken(token = hash)
            if (user == null) {
                Either.Left(value = UserServicesError.UserNotAuthenticated)
            } else {
                Either.Right(value = user)
            }
        }
    }

    /**
     * Method to get a user by id
     */
    fun getUserById(userId: Int): UserByIdResult {
        if (userId <= 0) return Either.Left(value = UserServicesError.InvalidData)
        return transactionManager.run {
            val user = it.usersRepository.getUserById(id = userId)
            if (user == null) {
                Either.Left(value = UserServicesError.UserNotFound)
            } else {
                Either.Right(value = user)
            }
        }
    }

    /**
     * Method to create a request to create a user as teacher
     */
    fun createTeacher(teacher: TeacherInput): TeacherCreationResult {
        if (teacher.isNotValid()) return Either.Left(value = UserServicesError.InvalidData)
        val hash = tokenHash.getTokenHash(teacher.token)
        val githubTokenHash = tokenHash.getTokenHash(teacher.githubToken)
        return transactionManager.run {
            if (it.usersRepository.checkIfGithubUsernameExists(githubUsername = teacher.githubUsername)) {
                return@run Either.Left(value = UserServicesError.GithubUserNameInUse)
            }
            if (it.usersRepository.checkIfEmailExists(email = teacher.email)) {
                return@run Either.Left(value = UserServicesError.EmailInUse)
            }
            if (it.usersRepository.checkIfGithubIdExists(githubId = teacher.githubId)) {
                return@run Either.Left(value = UserServicesError.GithubIdInUse)
            }
            if (it.usersRepository.checkIfTokenExists(token = hash)) {
                return@run Either.Left(value = UserServicesError.TokenInUse)
            }
            if (it.usersRepository.checkIfGithubTokenExists(githubToken = githubTokenHash)) {
                return@run Either.Left(value = UserServicesError.GithubTokenInUse)
            }
            val teacherRes = it.usersRepository.createTeacher(
                teacher = TeacherInput(
                    email = teacher.email,
                    githubUsername = teacher.githubUsername,
                    githubId = teacher.githubId,
                    token = hash,
                    name = teacher.name,
                    githubToken = githubTokenHash,
                ),
            )
            if (teacherRes == null) {
                Either.Left(value = UserServicesError.ErrorCreatingUser)
            } else {
                Either.Right(value = teacherRes)
            }
        }
    }

    /**
     * Method to create a user as student
     */
    fun createStudent(student: StudentInput): StudentCreationResult {
        if (student.isNotValid()) return Either.Left(value = UserServicesError.InvalidData)
        val hash = tokenHash.getTokenHash(token = student.token)
        return transactionManager.run {
            if (it.usersRepository.checkIfGithubUsernameExists(githubUsername = student.githubUsername)) {
                return@run Either.Left(value = UserServicesError.GithubUserNameInUse)
            }
            if (it.usersRepository.checkIfEmailExists(email = student.email)) {
                return@run Either.Left(value = UserServicesError.EmailInUse)
            }
            if (it.usersRepository.checkIfGithubIdExists(githubId = student.githubId)) {
                return@run Either.Left(value = UserServicesError.GithubIdInUse)
            }
            if (it.usersRepository.checkIfTokenExists(token = hash)) {
                return@run Either.Left(value = UserServicesError.TokenInUse)
            }
            if (student.schoolId != null && it.usersRepository.checkIfSchoolIdExists(schoolId = student.schoolId)) {
                return@run Either.Left(value = UserServicesError.SchoolIdInUse)
            }
            val studentRes = it.usersRepository.createStudent(
                student = StudentInput(
                    name = student.name,
                    email = student.email,
                    githubUsername = student.githubUsername,
                    schoolId = student.schoolId,
                    token = hash,
                    githubId = student.githubId,
                ),
            )
            if (studentRes == null) {
                Either.Left(value = UserServicesError.ErrorCreatingUser)
            } else {
                Either.Right(value = studentRes)
            }
        }
    }

    /**
     * Method to get a user by GitHub id
     */
    fun getUserByGithubId(githubId: Long): UserByGithubIdResult {
        if (githubId <= 0) return Either.Left(value = UserServicesError.InvalidGithubId)
        return transactionManager.run {
            val user = it.usersRepository.getUserByGithubId(githubId = githubId)
            if (user == null) {
                Either.Left(value = UserServicesError.UserNotFound)
            } else {
                Either.Right(value = user)
            }
        }
    }
}
