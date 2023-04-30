package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.PendingTeacher
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.domain.input.ApplyInput
import com.isel.leic.ps.ion_classcode.http.model.input.TeachersPendingInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeacherPending
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.repository.transaction.Transaction
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.tokenHash.TokenHash
import com.isel.leic.ps.ion_classcode.utils.Result
import com.isel.leic.ps.ion_classcode.utils.cypher.AESDecrypt
import com.isel.leic.ps.ion_classcode.utils.cypher.AESEncrypt
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias TeacherCreationResult = Result<TeacherServicesError, Teacher>
typealias PendingTeacherCreationResult = Result<TeacherServicesError, PendingTeacher>
typealias TeacherPendingResponse = Result<TeacherServicesError, List<TeacherPending>>
typealias TeachersApproveResponse = Result<TeacherServicesError, List<TeacherPending>>
typealias TeachersGetGithubTokenResponse = Result<TeacherServicesError, String>
typealias UpdateTeacherGithubTokenResult = Result<TeacherServicesError, Unit>

/**
 * Error codes for the services
 */
sealed class TeacherServicesError {
    object CourseNotFound : TeacherServicesError()
    object TeacherNotFound : TeacherServicesError()
    object InvalidData : TeacherServicesError()
    object GithubUserNameInUse : TeacherServicesError()
    object GithubIdInUse : TeacherServicesError()
    object EmailInUse : TeacherServicesError()
    object TokenInUse : TeacherServicesError()
    object InternalError: TeacherServicesError()
}

@Component
class TeacherServices(
    private val transactionManager: TransactionManager,
    private val tokenHash: TokenHash,
) {

    /**
     * Method to create a request to create a user as a teacher
     */
    fun createTeacher(githubId: Long): TeacherCreationResult {
        return transactionManager.run {
            val teacher = it.usersRepository.getPendingUserByGithubId(githubId) ?: Result.Problem(TeacherServicesError.TeacherNotFound)
            if (teacher is PendingTeacher) {
                if (it.usersRepository.checkIfGithubUsernameExists(teacher.githubUsername)) Result.Problem(
                    TeacherServicesError.GithubUserNameInUse
                )
                if (it.usersRepository.checkIfEmailExists(teacher.email)) Result.Problem(TeacherServicesError.EmailInUse)
                if (it.usersRepository.checkIfGithubIdExists(teacher.githubId)) Result.Problem(TeacherServicesError.GithubIdInUse)
                if (it.usersRepository.checkIfTokenExists(teacher.token)) Result.Problem(TeacherServicesError.TokenInUse)
                val teacherRes = it.usersRepository.createTeacher(
                    TeacherInput(
                        name = teacher.name,
                        email = teacher.email,
                        githubUsername = teacher.githubUsername,
                        githubId = teacher.githubId,
                        token = teacher.token,
                        githubToken = teacher.githubToken,
                    )
                )
                if (teacherRes == null) Result.Problem(TeacherServicesError.InternalError) else Result.Success(teacherRes)
            } else {
                Result.Problem(TeacherServicesError.TeacherNotFound)
            }
        }
    }

    /**
     * Method to create pending a user as a teacher
     */
    fun createPendingTeacher(teacher: TeacherInput): PendingTeacherCreationResult {
        if (teacher.isNotValid()) return Result.Problem(TeacherServicesError.InternalError)
        val hash = tokenHash.getTokenHash(teacher.token)
        val githubToken = AESEncrypt.encrypt(teacher.githubToken)
        return transactionManager.run {
            val teacherRes = it.usersRepository.createPendingTeacher(
               TeacherInput(
                    name = teacher.name,
                    email = teacher.email,
                    githubUsername = teacher.githubUsername,
                    githubId = teacher.githubId,
                    token = hash,
                    githubToken = githubToken,
                ),
            )
            it.applyRequestRepository.createApplyRequest(ApplyInput( teacherRes.id))
            Result.Success(teacherRes)
        }
    }

    /**
     * Method to get all the teachers that need approval
     */
    fun getTeachersNeedingApproval(): TeacherPendingResponse {
        return transactionManager.run {
            Result.Success(getTeachersNeedingApproval(it))
        }
    }

    /**
     * Method to approve or reject a teacher
     */
    fun approveTeachers(teachers: TeachersPendingInputModel): TeachersApproveResponse {
        if (teachers.isNotValid()) return Result.Problem(TeacherServicesError.InvalidData)
        return transactionManager.run {
            teachers.approved.map { teacherRequest ->
                it.applyRequestRepository.changeApplyRequestState(teacherRequest, "Accepted")
            }
            teachers.rejected.map { teacherRequest ->
                it.applyRequestRepository.changeApplyRequestState(teacherRequest, "Rejected")
            }
            Result.Success(getTeachersNeedingApproval(it))
        }
    }

    /**
     * Method to get the GitHub token of a teacher
     */
    fun getTeacherGithubToken(teacherId: Int): TeachersGetGithubTokenResponse {
        return transactionManager.run {
            val githubToken = it.usersRepository.getTeacherGithubToken(teacherId)
            if (githubToken == null) {
                Result.Problem(TeacherServicesError.InternalError)
            } else {
                Result.Success(AESDecrypt.decrypt(githubToken))
            }
        }
    }

    fun updateTeacherGithubToken(teacherId: Int, token: String): UpdateTeacherGithubTokenResult {
        val githubTokenHash = AESEncrypt.encrypt(token)
        return transactionManager.run {
            val user = it.usersRepository.getTeacher(teacherId)
            if (user == null) Result.Problem(TeacherServicesError.InternalError)
            Result.Success(it.usersRepository.updateTeacherGithubToken(teacherId, githubTokenHash))
        }
    }

    /**
     * Function to handle errors from teacher
     */
    fun problem(error: TeacherServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            is TeacherServicesError.CourseNotFound -> Problem.courseNotFound
            is TeacherServicesError.TeacherNotFound -> Problem.userNotFound
            is TeacherServicesError.InvalidData -> Problem.invalidInput
            is TeacherServicesError.TokenInUse -> Problem.internalError
            is TeacherServicesError.EmailInUse -> Problem.internalError
            is TeacherServicesError.GithubIdInUse -> Problem.internalError
            is TeacherServicesError.GithubUserNameInUse -> Problem.internalError
            is TeacherServicesError.InternalError -> Problem.internalError
        }
    }

    private fun getTeachersNeedingApproval(transaction: Transaction): List<TeacherPending> {
        val requestsPending = transaction.applyRequestRepository.getApplyRequests().filter { request ->
            request.state == "Pending"
        }
        return requestsPending.mapNotNull { apply ->
            val teacher = transaction.applyRequestRepository.getPendingTeacherByApply(apply.id) ?: return@mapNotNull null
            TeacherPending(
                id = teacher.id,
                name = teacher.name,
                email = teacher.email,
                applyRequestId = apply.id
            )
        }
    }
}
