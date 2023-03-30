package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias StudentCoursesResponse = Either<StudentServicesError, List<Course>>
typealias StudentSchoolIdResponse = Either<StudentServicesError, Int>


sealed class StudentServicesError {
    object CourseNotFound : StudentServicesError()
    object UserNotFound : StudentServicesError()
}

@Component
class StudentServices(
    private val transactionManager: TransactionManager,
) {

    fun getCourses(studentId: Int): StudentCoursesResponse {
        return transactionManager.run {
            val courses = it.courseRepository.getAllStudentCourses(studentId)
            Either.Right(courses)
            // TODO("Check options to be Either.Left")
        }
    }

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

}
