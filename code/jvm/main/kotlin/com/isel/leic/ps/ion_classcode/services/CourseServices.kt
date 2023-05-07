package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.CourseWithClassrooms
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.http.model.input.CourseInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseArchivedResult
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias CourseResponse = Result<CourseServicesError, CourseWithClassrooms>
typealias CourseCreatedResponse = Result<CourseServicesError, Course>
typealias CourseArchivedResponse = Result<CourseServicesError, CourseArchivedResult>
typealias LeaveCourseResponse = Result<CourseServicesError, Course>

/**
 * Error codes for the services
 */
sealed class CourseServicesError {
    object CourseNotFound : CourseServicesError()
    object UserNotInCourse : CourseServicesError()
    object NotTeacher : CourseServicesError()
    object InvalidInput : CourseServicesError()
    object CourseArchived : CourseServicesError()
    object CourseNameAlreadyExists : CourseServicesError()
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
    fun getCourseById(courseId: Int, userId: Int, student: Boolean): CourseResponse {
        return transactionManager.run {
            if (it.usersRepository.getUserById(userId) == null) Result.Problem(CourseServicesError.InternalError)
            val course = it.courseRepository.getCourse(courseId) ?: return@run Result.Problem(CourseServicesError.CourseNotFound)
            val classrooms = it.courseRepository.getCourseUserClassrooms(courseId, userId, student)
            val students = it.courseRepository.getStudentInCourse(courseId)
            Result.Success(
                CourseWithClassrooms(
                    id = course.id,
                    orgUrl = course.orgUrl,
                    name = course.name,
                    orgId = course.orgId,
                    teachers = course.teachers,
                    isArchived = course.isArchived,
                    students = students,
                    classrooms = classrooms,
                ),
            )
        }
    }

    /**
     * Method that creates a course
     */
    fun createCourse(courseInfo: CourseInputModel, teacherId: Int): CourseCreatedResponse {
        if (courseInfo.isNotValid()) return Result.Problem(CourseServicesError.InvalidInput)
        return transactionManager.run {
            if (it.usersRepository.getTeacher(teacherId) == null) Result.Problem(CourseServicesError.InternalError)
            val courseByOrg = it.courseRepository.getCourseByOrg(courseInfo.orgUrl)
            if (courseByOrg != null) {
                Result.Success(it.courseRepository.addTeacherToCourse(teacherId, courseByOrg.id))
            } else if (it.courseRepository.checkIfCourseNameExists(courseInfo.name)) {
                Result.Problem(CourseServicesError.CourseNameAlreadyExists)
            }
            val id = it.courseRepository.createCourse(CourseInput(courseInfo.orgUrl, courseInfo.name, courseInfo.orgId, teacherId)).id
            val course = it.courseRepository.addTeacherToCourse(teacherId, id)
            Result.Success(course)
        }
    }

    /**
     * Method that archives or deletes a course
     * If the course has classrooms, it archives it
     */
    fun archiveOrDeleteCourse(courseId: Int): CourseArchivedResponse {
        return transactionManager.run {
            val course = it.courseRepository.getCourse(courseId) ?: return@run Result.Problem(CourseServicesError.CourseNotFound)
            if (course.isArchived) Result.Problem(CourseServicesError.CourseArchived)
            val classrooms = it.courseRepository.getCourseAllClassrooms(courseId)
            if (classrooms.isNotEmpty()) {
                it.courseRepository.archiveCourse(courseId)
                Result.Success(CourseArchivedResult.CourseArchived)
            } else {
                it.courseRepository.deleteCourse(courseId)
                Result.Success(CourseArchivedResult.CourseDeleted)
            }
        }
    }

    /**
     * Method to request to leave a course
     */
    fun leaveCourse(courseId: Int, userId: Int): LeaveCourseResponse {
        return transactionManager.run {
            if (it.courseRepository.getCourse(courseId) == null) Result.Problem(CourseServicesError.CourseNotFound)
            if (!it.courseRepository.isStudentInCourse(userId, courseId)) Result.Problem(CourseServicesError.UserNotInCourse)
            val course = it.courseRepository.leaveCourse(courseId, userId)
            Result.Success(course)
        }
    }

    /**
     * Function to handle the errors
     */
    fun problem(error: CourseServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            CourseServicesError.CourseNotFound -> Problem.courseNotFound
            CourseServicesError.UserNotInCourse -> Problem.userNotInCourse
            CourseServicesError.NotTeacher -> Problem.notTeacher
            CourseServicesError.InvalidInput -> Problem.invalidInput
            CourseServicesError.CourseArchived -> Problem.invalidOperation
            CourseServicesError.CourseNameAlreadyExists -> Problem.conflict
            CourseServicesError.InternalError -> Problem.internalError
        }
    }
}
