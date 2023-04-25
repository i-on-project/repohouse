package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.http.model.input.TeachersPendingInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeacherPending
import com.isel.leic.ps.ion_classcode.repository.transaction.Transaction
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias TeacherCoursesResponse = Either<TeacherServicesError, List<Course>>
typealias TeacherPendingResponse = Either<TeacherServicesError, List<TeacherPending>>
typealias TeachersApproveResponse = Either<TeacherServicesError, List<TeacherPending>>
typealias TeachersGetGithubTokenResponse = Either<TeacherServicesError, String>

/**
 * Error codes for the services
 */
sealed class TeacherServicesError {
    object CourseNotFound : TeacherServicesError()
    object TeacherNotFound : TeacherServicesError()
    object InvalidData : TeacherServicesError()
}

@Component
class TeacherServices(
    private val transactionManager: TransactionManager,
) {

    /**
     * Method to get all the courses of a teacher
     */
    fun getCourses(teacherId: Int): TeacherCoursesResponse {
        if (teacherId < 0) return Either.Left(value = TeacherServicesError.InvalidData)
        return transactionManager.run {
            val courses = it.courseRepository.getAllUserCourses(userId = teacherId)
            Either.Right(value = courses)
        }
    }

    /**
     * Method to get all the teachers that need approval
     */
    fun getTeachersNeedingApproval(): TeacherPendingResponse {
        return transactionManager.run {
            Either.Right(value = getTeachersNeedingApproval(it))
        }
    }

    /**
     * Method to approve or reject a teacher
     */
    fun approveTeachers(teachers: TeachersPendingInputModel): TeachersApproveResponse {
        if (teachers.isNotValid()) return Either.Left(value = TeacherServicesError.InvalidData)
        return transactionManager.run {
            teachers.approved.map { teacherRequest ->
                it.requestRepository.changeStateRequest(teacherRequest, "Accepted")
            }
            teachers.rejected.map { teacherRequest ->
                it.requestRepository.changeStateRequest(teacherRequest, "Rejected")
            }
            Either.Right(value = getTeachersNeedingApproval(it))
        }
    }

    /**
     * Method to get the GitHub token of a teacher
     */
    fun getTeacherGithubToken(teacherId: Int): TeachersGetGithubTokenResponse {
        if (teacherId < 0) return Either.Left(value = TeacherServicesError.InvalidData)
        return transactionManager.run {
            val teacher = it.usersRepository.getTeacherGithubToken(teacherId)
            if (teacher == null) {
                Either.Left(value = TeacherServicesError.TeacherNotFound)
            } else {
                Either.Right(value = teacher)
            }
        }
    }

    private fun getTeachersNeedingApproval(transaction: Transaction): List<TeacherPending> {
        val requestsPending = transaction.applyRequestRepository.getApplyRequests().filter { request -> request.state == "Pending" }
        return requestsPending.mapNotNull { request ->
            val teacher = transaction.usersRepository.getUserById(request.creator)
            if (teacher is Teacher) {
                TeacherPending(teacher.name, teacher.email, teacher.id, request.id)
            } else {
                null
            }
        }
    }
}
