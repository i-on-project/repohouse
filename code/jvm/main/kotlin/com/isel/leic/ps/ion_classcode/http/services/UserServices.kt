package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
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
    fun checkAuthentication(bearerToken: String): UserAuthenticationResult {
        return transactionManager.run {
            val user = it.usersRepository.getUserByToken(bearerToken)
            if (user == null) {
                Either.Left(UserServicesError.UserNotAuthenticated)
            } else {
                Either.Right(user)
            }
        }
    }

    /**
     * Method to get a user by id
     */
    fun getUserById(userId: Int): UserByIdResult {
        if (userId <= 0) return Either.Left(UserServicesError.InvalidData)
        return transactionManager.run {
            val user = it.usersRepository.getUserById(userId)
            if (user == null) {
                Either.Left(UserServicesError.UserNotFound)
            } else {
                Either.Right(user)
            }
        }
    }

    /**
     * Method to create a request to create a user as teacher
     */
    fun createTeacher(teacher: TeacherInput): TeacherCreationResult {
        if (teacher.name.isEmpty() || teacher.email.isEmpty()) return Either.Left(UserServicesError.InvalidData)
        return transactionManager.run {
            val teacherId = it.usersRepository.createTeacher(teacher)
            if (teacherId <= 0) {
                Either.Left(UserServicesError.ErrorCreatingUser)
            } else {
                Either.Right(it.usersRepository.getUserById(teacherId) as Teacher)
            }
        }
    }


    /**
     * Method to create a user as student
     */
    fun createStudent(student: StudentInput): StudentCreationResult {
        if (student.name.isEmpty() || student.email.isEmpty()) return Either.Left(UserServicesError.InvalidData)
        return transactionManager.run {
            val studentId = it.usersRepository.createStudent(student)
            if (studentId <= 0) {
                Either.Left(UserServicesError.ErrorCreatingUser)
            } else {
                Either.Right(it.usersRepository.getUserById(studentId) as Student)
            }
        }
    }

    /**
     * Method to get a user by GitHub id
     */
    fun getUserByGithubId(githubId: Long): UserByGithubIdResult {
        if (githubId <= 0) return Either.Left(UserServicesError.InvalidGithubId)
        return transactionManager.run {
            val user = it.usersRepository.getUserByGithubId(githubId)
            if (user == null) {
                Either.Left(UserServicesError.UserNotFound)
            } else {
                Either.Right(user)
            }
        }
    }
}
