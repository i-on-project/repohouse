package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias StudentCoursesResponse = Either<StudentServicesError, List<Course>>
typealias StudentSchoolIdResponse = Either<StudentServicesError, Int>
typealias StudentSchoolIdUpdateResponse = Either<StudentServicesError, Boolean>

/**
 * Error codes for the services
 */
sealed class StudentServicesError {
    object CourseNotFound : StudentServicesError()
    object UserNotFound : StudentServicesError()
    object InvalidInput : StudentServicesError()
}

/**
 * Service to the student services
 */
@Component
class StudentServices(
    private val transactionManager: TransactionManager,
) {

    /**
     * Method to get all the courses of a student
     */
    fun getCourses(studentId: Int): StudentCoursesResponse {
        if (studentId <= 0) return Either.Left(value = StudentServicesError.InvalidInput)
        return transactionManager.run {
            val courses = it.courseRepository.getAllStudentCourses(studentId = studentId)
            Either.Right(value = courses)
        }
    }

    /**
     * Method to get the school id of a student
     */
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

    /**
     * Method to update the school id of a student
     */
    fun updateStudent(userId: Int, schoolId: Int): StudentSchoolIdUpdateResponse {
        if (userId <= 0 || schoolId <= 0) return Either.Left(value = StudentServicesError.InvalidInput)
        return transactionManager.run {
            it.usersRepository.updateStudentSchoolId(userId = userId, schoolId = schoolId)
            Either.Right(value = true)
        }
    }
}
