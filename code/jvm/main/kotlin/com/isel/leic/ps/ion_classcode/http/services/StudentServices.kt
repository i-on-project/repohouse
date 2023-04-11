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
typealias StudentSchoolIdUpdateResponse = Either<StudentServicesError, Unit>

/**
 * Error codes for the services
 */
sealed class StudentServicesError {
    object CourseNotFound : StudentServicesError()
    object UserNotFound : StudentServicesError()
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
        return transactionManager.run {
            val courses = it.courseRepository.getAllUserCourses(userId = studentId)
            Either.Right(courses)
        }
    }

    /**
     * Method to get the school id of a student
     */
    fun getStudentSchoolId(studentId: Int): StudentSchoolIdResponse {
        return transactionManager.run {
            val schoolId = it.usersRepository.getStudentSchoolId(studentId)
            if (schoolId != null) {
                Either.Right(schoolId)
            } else {
                Either.Left(StudentServicesError.UserNotFound)
            }
        }
    }

    /**
     * Method to update the school id of a student
     */
    fun updateStudent(userId: Int, schoolId: Int): StudentSchoolIdUpdateResponse {
        return transactionManager.run {
            val school = it.usersRepository.updateStudentSchoolId(userId, schoolId)
            Either.Right(school)
        }
    }
}
