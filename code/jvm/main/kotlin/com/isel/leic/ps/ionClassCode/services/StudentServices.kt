package com.isel.leic.ps.ionClassCode.services

import com.isel.leic.ps.ionClassCode.domain.PendingStudent
import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.input.StudentInput
import com.isel.leic.ps.ionClassCode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ionClassCode.http.model.problem.Problem
import com.isel.leic.ps.ionClassCode.repository.transaction.TransactionManager
import com.isel.leic.ps.ionClassCode.tokenHash.TokenHash
import com.isel.leic.ps.ionClassCode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias StudentCreationResult = Result<StudentServicesError, Student>
typealias PendingStudentCreationResult = Result<StudentServicesError, PendingStudent>
typealias StudentSchoolIdResponse = Result<StudentServicesError, Int>

/**
 * Error codes for the services
 */
sealed class StudentServicesError {
    object StudentNotFound : StudentServicesError()
    object InvalidData : StudentServicesError()
    object GithubUserNameInUse : StudentServicesError()
    object GithubIdInUse : StudentServicesError()
    object EmailInUse : StudentServicesError()
    object TokenInUse : StudentServicesError()
    object SchoolIdInUse : StudentServicesError()
    object InternalError : StudentServicesError()
}

/**
 * Service to the student services
 */
@Component
class StudentServices(
    private val transactionManager: TransactionManager,
    private val tokenHash: TokenHash,
) {

    /**
     * Method to create a user as a student
     */
    fun createStudent(githubId: Long, schoolId: Int): StudentCreationResult {
        if (schoolId <= 0) return Result.Problem(StudentServicesError.InvalidData)
        return transactionManager.run {
            val student = it.usersRepository.getPendingStudentByGithubId(githubId) ?: return@run Result.Problem(StudentServicesError.StudentNotFound)
            if (it.usersRepository.checkIfSchoolIdExists(schoolId)) return@run Result.Problem(StudentServicesError.SchoolIdInUse)
            val studentRes = it.usersRepository.createStudent(
                StudentInput(
                    name = student.name,
                    email = "A$schoolId@alunos.isel.pt",
                    githubUsername = student.githubUsername,
                    githubId = student.githubId,
                    token = student.token,
                    schoolId = schoolId,
                ),
            )
            if (studentRes == null) return@run Result.Problem(StudentServicesError.InternalError) else Result.Success(studentRes)
        }
    }

    /**
     * Method to create a pending user as a student
     */
    fun createPendingStudent(student: StudentInput): PendingStudentCreationResult {
        if (student.isNotValid()) return Result.Problem(StudentServicesError.InternalError)
        return transactionManager.run {
            val hash = tokenHash.getTokenHash(student.token)
            if (it.usersRepository.checkIfGithubUsernameExists(student.githubUsername)) return@run Result.Problem(StudentServicesError.GithubUserNameInUse)
            if (it.usersRepository.checkIfEmailExists(student.email)) return@run Result.Problem(StudentServicesError.EmailInUse)
            if (it.usersRepository.checkIfGithubIdExists(student.githubId)) return@run Result.Problem(StudentServicesError.GithubIdInUse)
            if (it.usersRepository.checkIfTokenExists(hash)) return@run Result.Problem(StudentServicesError.TokenInUse)
            val studentRes = it.usersRepository.createPendingStudent(
                StudentInput(
                    name = student.name,
                    email = student.email,
                    githubUsername = student.githubUsername,
                    githubId = student.githubId,
                    token = hash,
                    schoolId = student.schoolId,
                ),
            )
            return@run Result.Success(studentRes)
        }
    }

    /**
     * Method to get the school id of a student
     */
    fun getStudentSchoolId(studentId: Int): StudentSchoolIdResponse {
        return transactionManager.run {
            val schoolId = it.usersRepository.getStudentSchoolId(studentId)
            if (schoolId != null) {
                return@run Result.Success(schoolId)
            } else {
                return@run Result.Problem(StudentServicesError.InternalError)
            }
        }
    }

    /**
     * Function to handle errors from student
     */
    fun problem(error: StudentServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            is StudentServicesError.StudentNotFound -> Problem.userNotFound
            is StudentServicesError.InvalidData -> Problem.invalidInput
            is StudentServicesError.EmailInUse -> Problem.internalError
            is StudentServicesError.GithubIdInUse -> Problem.internalError
            is StudentServicesError.GithubUserNameInUse -> Problem.internalError
            is StudentServicesError.TokenInUse -> Problem.internalError
            is StudentServicesError.SchoolIdInUse -> Problem.conflict
            is StudentServicesError.InternalError -> Problem.internalError
        }
    }
}
