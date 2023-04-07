package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.http.model.input.TeachersPendingInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeacherPending
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias TeacherCoursesResponse = Either<TeacherServicesError, List<Course>>
typealias TeacherPendingResponse = Either<TeacherServicesError, List<TeacherPending>>
typealias TeachersApproveResponse = Either<TeacherServicesError, Boolean>

sealed class TeacherServicesError {
    object CourseNotFound : TeacherServicesError()
    object TeacherNotFound : TeacherServicesError()
}

@Component
class TeacherServices(
    private val transactionManager: TransactionManager,
) {

    fun getCourses(teacherId: Int): TeacherCoursesResponse {
        return transactionManager.run {
            val courses = it.courseRepository.getAllUserCourses(userId = teacherId)
            Either.Right(courses)
        }
    }

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
            Either.Right(teachers)
        }
    }

    fun approveTeachers(teachers: TeachersPendingInputModel): TeachersApproveResponse {
        return transactionManager.run {
            teachers.approved.map { teacherRequest ->
                it.requestRepository.changeStateRequest(teacherRequest, "Approved")
            }
            teachers.rejected.map { teacherRequest ->
                it.requestRepository.changeStateRequest(teacherRequest, "Rejected")
            }
            Either.Right(true)
        }
    }

    fun getTeacherGithubToken(teacherId: Int): Either<TeacherServicesError, String> {
        return transactionManager.run {
            val teacher = it.usersRepository.getTeacherGithubToken(teacherId)
            if (teacher == null) {
                Either.Left(TeacherServicesError.TeacherNotFound)
            } else {
                Either.Right(teacher)
            }
        }
    }
}
