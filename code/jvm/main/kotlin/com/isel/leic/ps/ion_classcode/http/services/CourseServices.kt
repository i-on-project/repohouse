package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.CourseWithClassrooms
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.http.model.input.CourseInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseArchivedModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias CourseResponse = Either<CourseServicesError, CourseWithClassrooms>
typealias CourseCreatedResponse = Either<CourseServicesError, Course>
typealias CourseArchivedResponse = Either<CourseServicesError, CourseArchivedModel>
typealias LeaveCourseResponse = Either<CourseServicesError, Course>

/**
 * Error codes for the services
 */
sealed class CourseServicesError {
    object CourseNotFound : CourseServicesError()
    object CourseAlreadyExists : CourseServicesError()
    object UserInCourse : CourseServicesError()
    object UserNotInCourse : CourseServicesError()
    object UserNotFound : CourseServicesError()
    object NotStudent : CourseServicesError()
    object NotTeacher : CourseServicesError()
    object InvalidInput : CourseServicesError()
    object CourseArchived : CourseServicesError()
}

/**
 * Services for the course
 */
@Component
class CourseServices(
    private val transactionManager: TransactionManager,
) {

    /**
     * Method that gets a course
     */
    fun getCourseById(courseId: Int,userId: Int): CourseResponse {
        return transactionManager.run {
            val course = it.courseRepository.getCourse(courseId)
            val classrooms = it.courseRepository.getCourseUserClassrooms(courseId,userId)
            val students = it.courseRepository.getStudentInCourse(courseId)
            if (course == null) {
                Either.Left(CourseServicesError.CourseNotFound)
            } else {
                val teachers = it.courseRepository.getCourseTeachers(courseId)
                Either.Right(CourseWithClassrooms(course.id, course.orgUrl, course.name, teachers, course.isArchived, students, classrooms))
            }
        }
    }

    /**
     * Method that creates a course
     */
    fun createCourse(courseInfo: CourseInputModel): CourseCreatedResponse {
        return transactionManager.run {
            if (it.usersRepository.getTeacher(courseInfo.teacherId) == null) Either.Left(CourseServicesError.NotTeacher)
            if (courseInfo.orgUrl.isEmpty() || courseInfo.name.isEmpty()) Either.Left(CourseServicesError.InvalidInput)
            val courseByOrg = it.courseRepository.getCourseByOrg(courseInfo.orgUrl)
            val id = if (courseByOrg != null) {
                return@run Either.Right(it.courseRepository.addTeacherToCourse(teacherId = courseInfo.teacherId, courseId = courseByOrg.id))
            } else {
                it.courseRepository.createCourse(
                    CourseInput(
                        courseInfo.orgUrl,
                        courseInfo.name,
                        courseInfo.teacherId,
                    ),
                ).id
            }
            val course = it.courseRepository.getCourse(id)
            if (course == null) {
                Either.Left(CourseServicesError.CourseNotFound)
            } else {
                Either.Right(course)
            }
        }
    }

    /**
     * Method that archives or deletes a course
     * If the course has classrooms, it archives it
     */
    fun archiveOrDeleteCourse(courseId: Int): CourseArchivedResponse {
        return transactionManager.run {
            if (it.courseRepository.getCourse(courseId) == null) Either.Left(CourseServicesError.CourseNotFound)
            val course = it.courseRepository.getCourse(courseId)
            if (course == null) {
                Either.Left(CourseServicesError.CourseNotFound)
            } else if (course.isArchived) Either.Left(CourseServicesError.CourseArchived)
            val classrooms = it.courseRepository.getCourseAllClassrooms(courseId)
            if (classrooms.isNotEmpty()) {
                it.courseRepository.archiveCourse(courseId)
                Either.Right(CourseArchivedModel.CourseArchived)
            } else {
                it.courseRepository.deleteCourse(courseId)
                Either.Right(CourseArchivedModel.CourseDeleted)
            }
        }
    }

    /**
     * Method to request to leave a course
     */
    fun leaveCourse(courseId: Int, userId: Int): LeaveCourseResponse {
        return transactionManager.run {
            if (it.courseRepository.getCourse(courseId) == null) Either.Left(CourseServicesError.CourseNotFound)
            if (it.usersRepository.getUserById(userId) == null) Either.Left(CourseServicesError.UserNotFound)
            if (!it.courseRepository.isStudentInCourse(userId, courseId)) Either.Left(CourseServicesError.UserNotInCourse)
            val course = it.courseRepository.leaveCourse(courseId, userId)
            Either.Right(course)
        }
    }
}
