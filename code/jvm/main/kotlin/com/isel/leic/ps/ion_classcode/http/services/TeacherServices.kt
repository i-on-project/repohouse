package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.http.model.input.TeachersPendingInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeacherPending
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias TeacherCoursesResponse = Either<TeacherServicesError, List<Course>>
typealias TeacherPendingResponse = Either<TeacherServicesError, List<TeacherPending>>
typealias TeachersApproveResponse = Either<TeacherServicesError, Boolean>
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
            val requestsPending = it.applyRequestRepository.getApplyRequests().filter { request -> request.state == "Pending" }
            val teachers = requestsPending
                .mapNotNull { request ->
                    val teacher = it.usersRepository.getUserById(request.creator)
                    if (teacher is Teacher) {
                        TeacherPending(teacher.name, teacher.email, teacher.id, request.id)
                    } else {
                        null
                    }
                }
            Either.Right(value = teachers)
        }
    }

    /**
     * Method to approve or reject a teacher
     */
    fun approveTeachers(teachers: TeachersPendingInputModel): TeachersApproveResponse {
        if (teachers.isNotValid()) return Either.Left(value = TeacherServicesError.InvalidData)
        return transactionManager.run {
            teachers.approved.map { teacherRequest ->
                it.requestRepository.changeStateRequest(teacherRequest, "Approved")
            }
            teachers.rejected.map { teacherRequest ->
                it.requestRepository.changeStateRequest(teacherRequest, "Rejected")
            }
            Either.Right(value = true)
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
}
