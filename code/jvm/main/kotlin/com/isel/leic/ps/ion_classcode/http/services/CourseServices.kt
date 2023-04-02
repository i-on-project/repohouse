package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.http.model.input.CourseInputModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias CourseResponse = Either<CourseServicesError, Course>
typealias CourseCreatedResponse = Either<CourseServicesError, Int>
typealias CourseClassrooms = Either<CourseServicesError, List<Classroom>>

sealed class CourseServicesError {
    object CourseNotFound : CourseServicesError()
}


@Component
class CourseServices(
    private val transactionManager: TransactionManager,
) {
    fun getCourseById(courseId: Int): CourseResponse {
        return transactionManager.run {
            val course = it.courseRepository.getCourse(courseId)
            val classrooms = it.courseRepository.getCourseClassrooms(courseId)
            if (course == null) {
                Either.Left(CourseServicesError.CourseNotFound)
            } else {
                Either.Right(Course(course.id, course.orgUrl, course.name, course.teacherId, classrooms))
            }
        }
    }

    fun createCourse(courseInfo: CourseInputModel): CourseCreatedResponse {
        return transactionManager.run {
            val id = it.courseRepository.createCourse(CourseInput(courseInfo.orgUrl, courseInfo.name, courseInfo.teacherId))
            Either.Right(id)
        }
    }

}