package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.CourseWithClassrooms
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.http.model.input.CourseInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseArchivedOutputModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias CourseResponse = Either<CourseServicesError, CourseWithClassrooms>
typealias CourseCreatedResponse = Either<CourseServicesError, Course>
typealias CourseArchivedResponse = Either<CourseServicesError, CourseArchivedOutputModel>
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
    object CourseNameAlreadyExists : CourseServicesError()
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
    fun getCourseById(courseId: Int, userId: Int): CourseResponse {
        if (courseId <= 0 || userId <= 0) return Either.Left(value = CourseServicesError.InvalidInput)
        return transactionManager.run {
            val course = it.courseRepository.getCourse(courseId = courseId)
                ?: return@run Either.Left(value = CourseServicesError.CourseNotFound)
            if (it.usersRepository.getUserById(id = userId) == null) {
                return@run Either.Left(value = CourseServicesError.UserNotFound)
            }
            val classrooms = it.courseRepository.getCourseUserClassrooms(courseId = courseId, userId = userId)
            val students = it.courseRepository.getStudentInCourse(courseId = courseId)
            Either.Right(value = CourseWithClassrooms(id = course.id, orgUrl = course.orgUrl, name = course.name, teachers = course.teachers, isArchived = course.isArchived, students = students, classrooms = classrooms))
        }
    }

    /**
     * Method that creates a course
     */
    fun createCourse(courseInfo: CourseInputModel): CourseCreatedResponse {
        if (courseInfo.isNotValid()) return Either.Left(value = CourseServicesError.InvalidInput)
        return transactionManager.run {
            if (it.courseRepository.checkIfCourseNameExists(name = courseInfo.name)) {
                return@run Either.Left(value = CourseServicesError.CourseNameAlreadyExists)
            }
            if (it.usersRepository.getTeacher(teacherId = courseInfo.teacherId) == null) {
                return@run Either.Left(value = CourseServicesError.NotTeacher)
            }
            val courseByOrg = it.courseRepository.getCourseByOrg(orgUrl = courseInfo.orgUrl)
            if (courseByOrg != null) {
                return@run Either.Right(value = it.courseRepository.addTeacherToCourse(teacherId = courseInfo.teacherId, courseId = courseByOrg.id))
            }
            val id = it.courseRepository.createCourse(course = CourseInput(orgUrl = courseInfo.orgUrl, name = courseInfo.name, teacherId = courseInfo.teacherId)).id
            val course = it.courseRepository.getCourse(courseId = id)
            if (course == null) {
                Either.Left(value = CourseServicesError.CourseNotFound)
            } else {
                Either.Right(value = course)
            }
        }
    }

    /**
     * Method that archives or deletes a course
     * If the course has classrooms, it archives it
     */
    fun archiveOrDeleteCourse(courseId: Int): CourseArchivedResponse {
        if (courseId <= 0) return Either.Left(value = CourseServicesError.InvalidInput)
        return transactionManager.run {
            val course = it.courseRepository.getCourse(courseId = courseId)
                ?: return@run Either.Left(value = CourseServicesError.CourseNotFound)
            if (course.isArchived) return@run Either.Left(value = CourseServicesError.CourseArchived)
            val classrooms = it.courseRepository.getCourseAllClassrooms(courseId = courseId)
            if (classrooms.isNotEmpty()) {
                it.courseRepository.archiveCourse(courseId = courseId)
                Either.Right(value = CourseArchivedOutputModel.CourseArchived)
            } else {
                it.courseRepository.deleteCourse(courseId = courseId)
                Either.Right(value = CourseArchivedOutputModel.CourseDeleted)
            }
        }
    }

    /**
     * Method to request to leave a course
     */
    fun leaveCourse(courseId: Int, userId: Int): LeaveCourseResponse {
        if (courseId <= 0 || userId <= 0) return Either.Left(value = CourseServicesError.InvalidInput)
        return transactionManager.run {
            if (it.courseRepository.getCourse(courseId = courseId) == null) return@run Either.Left(value = CourseServicesError.CourseNotFound)
            if (it.usersRepository.getUserById(id = userId) == null) return@run Either.Left(value = CourseServicesError.UserNotFound)
            if (!it.courseRepository.isStudentInCourse(studentId = userId, courseId = courseId)) return@run Either.Left(value = CourseServicesError.UserNotInCourse)
            val course = it.courseRepository.leaveCourse(courseId = courseId, studentId = userId)
            Either.Right(value = course)
        }
    }
}
