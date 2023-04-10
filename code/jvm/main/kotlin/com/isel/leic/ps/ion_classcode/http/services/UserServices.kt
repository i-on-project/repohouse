package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias UserAuthenticationResult = Either<UserServicesError, User>
typealias UserByIdResult = Either<UserServicesError, User>
typealias UserByGithubIdResult = Either<UserServicesError, User>
typealias TeacherCreationResult = Either<UserServicesError, Teacher>
typealias StudentCreationResult = Either<UserServicesError, Student>

sealed class UserServicesError {
    object UserNotFound : UserServicesError()
    object UserNotAuthenticated : UserServicesError()
    object InvalidGithubId : UserServicesError()
    object InvalidData : UserServicesError()
    object ErrorCreatingUser : UserServicesError()
    object InvalidBearerToken : UserServicesError()
    object GithubUserNameInUse : UserServicesError()
    object EmailInUse : UserServicesError()
    object GithubIdInUse : UserServicesError()
    object TokenInUse : UserServicesError()
    object GithubTokenInUse : UserServicesError()
    object SchoolIdInUse : UserServicesError()
}

@Component
class UserServices(
    private val transactionManager: TransactionManager,
) {
    fun createTeacher(teacher: TeacherInput): TeacherCreationResult {
        if (teacher.isNotValid()) return Either.Left(value = UserServicesError.InvalidData)
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
            if (it.usersRepository.checkIfTokenExists(token = teacher.token)) {
                return@run Either.Left(value = UserServicesError.TokenInUse)
            }
            if (it.usersRepository.checkIfGithubTokenExists(githubToken = teacher.githubToken)) {
                return@run Either.Left(value = UserServicesError.GithubTokenInUse)
            }
            val teacherRes = it.usersRepository.createTeacher(teacher = teacher)
            if (teacherRes == null) {
                Either.Left(value = UserServicesError.ErrorCreatingUser)
            } else {
                Either.Right(value = teacherRes)
            }
        }
    }

    fun createStudent(student: StudentInput): StudentCreationResult {
        if (student.isNotValid()) return Either.Left(value = UserServicesError.InvalidData)
        return transactionManager.run {
            val studentRes = it.usersRepository.createStudent(student = student)
            if (it.usersRepository.checkIfGithubUsernameExists(githubUsername = student.githubUsername)) {
                return@run Either.Left(value = UserServicesError.GithubUserNameInUse)
            }
            if (it.usersRepository.checkIfEmailExists(email = student.email)) {
                return@run Either.Left(value = UserServicesError.EmailInUse)
            }
            if (it.usersRepository.checkIfGithubIdExists(githubId = student.githubId)) {
                return@run Either.Left(value = UserServicesError.GithubIdInUse)
            }
            if (it.usersRepository.checkIfTokenExists(token = student.token)) {
                return@run Either.Left(value = UserServicesError.TokenInUse)
            }
            if (student.schoolId != null && it.usersRepository.checkIfSchoolIdExists(schoolId = student.schoolId)) {
                return@run Either.Left(value = UserServicesError.SchoolIdInUse)
            }
            if (studentRes == null) {
                Either.Left(value = UserServicesError.ErrorCreatingUser)
            } else {
                Either.Right(value = studentRes)
            }
        }
    }
    fun checkAuthentication(bearerToken: String): UserAuthenticationResult {
        if (bearerToken.isEmpty()) return Either.Left(value = UserServicesError.InvalidBearerToken)
        return transactionManager.run {
            val user = it.usersRepository.getUserByToken(token = bearerToken)
            if (user == null) {
                Either.Left(value = UserServicesError.UserNotAuthenticated)
            } else {
                Either.Right(value = user)
            }
        }
    }

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
