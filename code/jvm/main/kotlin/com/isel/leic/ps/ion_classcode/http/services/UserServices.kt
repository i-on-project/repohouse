package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.PendingStudent
import com.isel.leic.ps.ion_classcode.domain.PendingTeacher
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.tokenHash.TokenHash
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias UserAuthenticationResult = Either<UserServicesError, User>
typealias UserByIdResult = Either<UserServicesError, User>
typealias UserByGithubIdResult = Either<UserServicesError, User>
typealias TeacherCreationResult = Either<UserServicesError, Teacher>
typealias StudentCreationResult = Either<UserServicesError, Student>
typealias PendingStudentCreationResult = Either<UserServicesError, PendingStudent>
typealias PendingTeacherCreationResult = Either<UserServicesError, PendingTeacher>

/**
 * Error codes for the services
 */
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

/**
 * Service to the user services
 */
@Component
class UserServices(
    private val transactionManager: TransactionManager,
    private val tokenHash: TokenHash
) {
    /**
     * Method to check the token from a user
     */
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
     * Method to create a request to create a user as a teacher
     */
    fun createTeacher(githubId: Long): TeacherCreationResult {
        return transactionManager.run {
            val teacher = it.usersRepository.getPendingUserByGithubId(githubId = githubId)
            if (teacher == null) {
                Either.Left(value = UserServicesError.UserNotFound)
            } else if (teacher is PendingTeacher){
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
                val teacherRes = it.usersRepository.createTeacher(teacher = TeacherInput(
                    name = teacher.name,
                    email = teacher.email,
                    githubUsername = teacher.githubUsername,
                    githubId = teacher.githubId,
                    token = teacher.token,
                    githubToken = teacher.githubToken
                )
                )
                if (teacherRes == null) {
                    Either.Left(value = UserServicesError.ErrorCreatingUser)
                } else {
                    Either.Right(value = teacherRes)
                }
            }else{
                Either.Left(value = UserServicesError.UserNotFound)
            }

        }
    }

    /**
     * Method to create pending a user as a teacher
     */
    fun createPendingTeacher(teacher: TeacherInput): PendingTeacherCreationResult {
        if (teacher.isNotValid()) return Either.Left(value = UserServicesError.InvalidData)
        return transactionManager.run {
            val hash = tokenHash.getTokenHash(teacher.token)
            val githubTokenHash = tokenHash.getTokenHash(teacher.githubToken)
            val teacherRes = it.usersRepository.createPendingTeacher(teacher = TeacherInput(
                name = teacher.name,
                email = teacher.email,
                githubUsername = teacher.githubUsername,
                githubId = teacher.githubId,
                token = hash,
                githubToken = githubTokenHash)
            )
            Either.Right(value = teacherRes)
        }
    }

    /**
     * Method to create a user as a student
     */
    fun createStudent(githubId: Long,schoolId:Int): StudentCreationResult {
        return transactionManager.run {
            val student = it.usersRepository.getPendingUserByGithubId(githubId = githubId)
            if (student == null) {
                Either.Left(value = UserServicesError.UserNotFound)
            } else if (student is PendingStudent) {
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
                if (it.usersRepository.checkIfSchoolIdExists(schoolId = schoolId)) {
                    return@run Either.Left(value = UserServicesError.SchoolIdInUse)
                }
                val studentRes = it.usersRepository.createStudent(student =
                StudentInput(
                    name = student.name,
                    email = student.email,
                    githubUsername = student.githubUsername,
                    githubId = student.githubId,
                    token = student.token,
                    schoolId = schoolId
                ))
                if (studentRes == null) {
                    Either.Left(value = UserServicesError.ErrorCreatingUser)
                } else {
                    Either.Right(value = studentRes)
                }
            } else {
                Either.Left(value = UserServicesError.UserNotFound)
            }
        }
    }

    /**
     * Method to create a pending user as a student
     */
    fun createPendingStudent(student: StudentInput): PendingStudentCreationResult {
        if (student.name.isEmpty() || student.email.isEmpty()) return Either.Left(UserServicesError.InvalidData)
        if (student.isNotValid()) return Either.Left(value = UserServicesError.InvalidData)
        return transactionManager.run {
            val hash = tokenHash.getTokenHash(student.token)
            val studentRes = it.usersRepository.createPendingStudent(student = StudentInput(
                name = student.name,
                email = student.email,
                githubUsername = student.githubUsername,
                githubId = student.githubId,
                token = hash,
                schoolId = student.schoolId))
            Either.Right(value = studentRes)
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


    /**
     * Method to get a pending user by GitHub id
     */
    fun getPendingUserByGithubId(githubId: Long): UserByGithubIdResult {
        if (githubId <= 0) return Either.Left(value = UserServicesError.InvalidGithubId)
        return transactionManager.run {
            val user = it.usersRepository.getPendingUserByGithubId(githubId = githubId)
            if (user == null) {
                Either.Left(value = UserServicesError.UserNotFound)
            } else {
                Either.Right(value = user)
            }
        }
    }


    /**
     * Method to delete pending users from the database after each day at 12:00 pm
     */

    @Scheduled(cron = "0 0 0 * * *")
    fun deletePendingUsers(){
        return transactionManager.run {
            it.usersRepository.deletePendingUsers()
        }
    }
}
