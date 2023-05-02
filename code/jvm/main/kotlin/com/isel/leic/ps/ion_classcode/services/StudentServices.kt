package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.PendingStudent
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.tokenHash.TokenHash
import com.isel.leic.ps.ion_classcode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias StudentCreationResult = Result<StudentServicesError, Student>
typealias PendingStudentCreationResult = Result<StudentServicesError, PendingStudent>
typealias StudentSchoolIdResponse = Result<StudentServicesError, Int>
typealias StudentSchoolIdUpdateResponse = Result<StudentServicesError, Boolean>

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
    object InternalError: StudentServicesError()
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
            val student = it.usersRepository.getPendingStudentByGithubId(githubId) ?: Result.Problem(StudentServicesError.StudentNotFound)
            if (student is PendingStudent) {
                if (it.usersRepository.checkIfGithubUsernameExists(student.githubUsername)) Result.Problem(
                    StudentServicesError.GithubUserNameInUse
                )
                if (it.usersRepository.checkIfEmailExists(student.email)) Result.Problem(StudentServicesError.EmailInUse)
                if (it.usersRepository.checkIfGithubIdExists(student.githubId)) Result.Problem(StudentServicesError.GithubIdInUse)
                if (it.usersRepository.checkIfTokenExists(student.token)) Result.Problem(StudentServicesError.TokenInUse)
                if (it.usersRepository.checkIfSchoolIdExists(schoolId)) Result.Problem(StudentServicesError.SchoolIdInUse)
                val studentRes = it.usersRepository.createStudent(
                    StudentInput(
                        name = student.name,
                        email = student.email,
                        githubUsername = student.githubUsername,
                        githubId = student.githubId,
                        token = student.token,
                        schoolId = schoolId,
                    )
                )
                if (studentRes == null) Result.Problem(StudentServicesError.InternalError) else Result.Success(studentRes)
            } else {
                Result.Problem(StudentServicesError.StudentNotFound)
            }
        }
    }

    /**
     * Method to create a pending user as a student
     */
    fun createPendingStudent(student: StudentInput): PendingStudentCreationResult {
        if (student.isNotValid()) return Result.Problem(StudentServicesError.InternalError)
        return transactionManager.run {
            val hash = tokenHash.getTokenHash(student.token)
            val studentRes = it.usersRepository.createPendingStudent(
                StudentInput(
                    name = student.name,
                    email = student.email,
                    githubUsername = student.githubUsername,
                    githubId = student.githubId,
                    token = hash,
                    schoolId = student.schoolId,
                )
            )
            Result.Success(studentRes)
        }
    }

    /**
     * Method to get the school id of a student
     */
    fun getStudentSchoolId(studentId: Int): StudentSchoolIdResponse {
        if (studentId <= 0) return Result.Problem(value = StudentServicesError.InvalidData)
        return transactionManager.run {
            val schoolId = it.usersRepository.getStudentSchoolId(id = studentId)
            if (schoolId != null) {
                Result.Success(value = schoolId)
            } else {
                Result.Problem(value = StudentServicesError.StudentNotFound)
            }
        }
    }

    /**
     * Method to update the school id of a student
     */
    fun updateStudent(userId: Int, schoolId: Int): StudentSchoolIdUpdateResponse {
        if (userId <= 0 || schoolId <= 0) return Result.Problem(value = StudentServicesError.InvalidData)
        return transactionManager.run {
            it.usersRepository.updateStudentSchoolId(userId = userId, schoolId = schoolId)
            Result.Success(value = true)
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
