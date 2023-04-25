package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.CourseWithClassrooms
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.http.model.input.CourseInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseArchivedOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.http.ResponseEntity
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
    object CourseUrlAlreadyExists : CourseServicesError()
    object InternalError : CourseServicesError()
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
        return transactionManager.run {
            val course = it.courseRepository.getCourse(courseId) ?: return@run Either.Left(CourseServicesError.CourseNotFound)
            val classrooms = it.courseRepository.getCourseUserClassrooms(courseId, userId)
            val students = it.courseRepository.getStudentInCourse(courseId)
            Either.Right(CourseWithClassrooms(
                id = course.id,
                orgUrl = course.orgUrl,
                name = course.name,
                teachers = course.teachers,
                isArchived = course.isArchived,
                students = students,
                classrooms = classrooms
            ))
        }
    }

    /**
     * Method that creates a course
     */
    fun createCourse(courseInfo: CourseInputModel, teacherId: Int): CourseCreatedResponse {
        if (courseInfo.isNotValid()) return Either.Left(CourseServicesError.InvalidInput)
        return transactionManager.run {
            if (it.usersRepository.getTeacher(teacherId) == null) {
                return@run Either.Left(CourseServicesError.NotTeacher)
            }
            val courseByOrg = it.courseRepository.getCourseByOrg(courseInfo.orgUrl)
            if (courseByOrg != null) {
                return@run Either.Right(it.courseRepository.addTeacherToCourse(teacherId, courseByOrg.id))
            } else {
                if (it.courseRepository.checkIfCourseNameExists(courseInfo.name)) {
                    return@run Either.Left(CourseServicesError.CourseNameAlreadyExists)
                }
            }
            val id = it.courseRepository.createCourse(CourseInput(courseInfo.orgUrl, courseInfo.name, teacherId)).id
            val course = it.courseRepository.addTeacherToCourse(teacherId, id)
            Either.Right(course)
        }
    }

    /**
     * Method that archives or deletes a course
     * If the course has classrooms, it archives it
     */
    fun archiveOrDeleteCourse(courseId: Int): CourseArchivedResponse {
        if (courseId <= 0) return Either.Left(CourseServicesError.CourseNotFound)
        return transactionManager.run {
            val course = it.courseRepository.getCourse(courseId) ?: return@run Either.Left(CourseServicesError.CourseNotFound)
            if (course.isArchived) return@run Either.Left(CourseServicesError.CourseArchived)
            val classrooms = it.courseRepository.getCourseAllClassrooms(courseId)
            if (classrooms.isNotEmpty()) {
                it.courseRepository.archiveCourse(courseId)
                Either.Right(CourseArchivedOutputModel.CourseArchived)
            } else {
                it.courseRepository.deleteCourse(courseId)
                Either.Right(CourseArchivedOutputModel.CourseDeleted)
            }
        }
    }

    /**
     * Method to request to leave a course
     */
    fun leaveCourse(courseId: Int, userId: Int): LeaveCourseResponse {
        return transactionManager.run {
            if (it.courseRepository.getCourse(courseId) == null) return@run Either.Left(CourseServicesError.CourseNotFound)
            if (!it.courseRepository.isStudentInCourse(userId, courseId)) return@run Either.Left(CourseServicesError.UserNotInCourse)
            val course = it.courseRepository.leaveCourse(courseId, userId)
            Either.Right(course)
        }
    }

    /**
     * Function to handle the errors
     */
    fun problem(error: CourseServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            CourseServicesError.CourseNotFound -> Problem.courseNotFound
            CourseServicesError.CourseAlreadyExists -> Problem.courseAlreadyExists
            CourseServicesError.UserInCourse -> Problem.userInCourse
            CourseServicesError.UserNotInCourse -> Problem.userNotInCourse
            CourseServicesError.UserNotFound -> Problem.userNotFound
            CourseServicesError.NotStudent -> Problem.notStudent
            CourseServicesError.NotTeacher -> Problem.notTeacher
            CourseServicesError.InvalidInput -> Problem.invalidInput
            CourseServicesError.CourseArchived -> Problem.invalidOperation
            CourseServicesError.CourseNameAlreadyExists -> Problem.conflict
            CourseServicesError.CourseUrlAlreadyExists -> Problem.conflict
            CourseServicesError.InternalError -> Problem.internalError
        }
    }
}
