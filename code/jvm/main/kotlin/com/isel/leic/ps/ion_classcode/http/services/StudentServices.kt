package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias StudentCoursesResponse = Either<StudentServicesError, List<Course>>
typealias StudentSchoolIdResponse = Either<StudentServicesError, Int>
typealias StudentSchoolIdUpdateResponse = Either<StudentServicesError, Boolean>

sealed class StudentServicesError {
    object CourseNotFound : StudentServicesError()
    object UserNotFound : StudentServicesError()
    object InvalidInput : StudentServicesError()
}

@Component
class StudentServices(
    private val transactionManager: TransactionManager,
) {

    fun getCourses(studentId: Int): StudentCoursesResponse {
        if (studentId <= 0) return Either.Left(value = StudentServicesError.InvalidInput)
        return transactionManager.run {
            val courses = it.courseRepository.getAllUserCourses(userId = studentId)
            Either.Right(value = courses)
        }
    }

    fun getStudentSchoolId(studentId: Int): StudentSchoolIdResponse {
        if (studentId <= 0) return Either.Left(value = StudentServicesError.InvalidInput)
        return transactionManager.run {
            val schoolId = it.usersRepository.getStudentSchoolId(id = studentId)
            if (schoolId != null) {
                Either.Right(value = schoolId)
            } else {
                Either.Left(value = StudentServicesError.UserNotFound)
            }
        }
    }

    fun updateStudent(userId: Int, schoolId: Int): StudentSchoolIdUpdateResponse {
        if (userId <= 0 || schoolId <= 0) return Either.Left(value = StudentServicesError.InvalidInput)
        return transactionManager.run {
            it.usersRepository.updateStudentSchoolId(userId = userId, schoolId = schoolId)
            Either.Right(value = true)
        }
    }
}
