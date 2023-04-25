package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.CourseWithClassrooms
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.http.model.input.CourseInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseArchivedOutputModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import com.isel.leic.ps.ion_classcode.utils.cypher.AESDecrypt
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias CourseResponse = Either<CourseServicesError, CourseWithClassrooms>
typealias CourseCreatedResponse = Either<CourseServicesError, Course>
typealias CourseArchivedResponse = Either<CourseServicesError, CourseArchivedOutputModel>
typealias LeaveCourseResponse = Either<CourseServicesError, Course>
typealias UserGithubTokenResponse = Either<CourseServicesError, String>

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
        if (courseId <= 0) return Either.Left(value = CourseServicesError.CourseNotFound)
        return transactionManager.run {
            val course = it.courseRepository.getCourse(courseId = courseId) ?: return@run Either.Left(value = CourseServicesError.CourseNotFound)
            val classrooms = it.courseRepository.getCourseUserClassrooms(courseId = courseId, userId = userId)
            val students = it.courseRepository.getStudentInCourse(courseId = courseId)
            Either.Right(value = CourseWithClassrooms(id = course.id, orgUrl = course.orgUrl, name = course.name, teachers = course.teachers, isArchived = course.isArchived, students = students, classrooms = classrooms))
        }
    }

    /**
     * Method that creates a course
     */
    fun createCourse(courseInfo: CourseInputModel, teacherId: Int): CourseCreatedResponse {
        if (courseInfo.isNotValid()) return Either.Left(value = CourseServicesError.InvalidInput)
        return transactionManager.run {
            if (it.usersRepository.getTeacher(teacherId = teacherId) == null) {
                return@run Either.Left(value = CourseServicesError.NotTeacher)
            }
            val courseByOrg = it.courseRepository.getCourseByOrg(orgUrl = courseInfo.orgUrl)
            if (courseByOrg != null) {
                return@run Either.Right(value = it.courseRepository.addTeacherToCourse(teacherId = teacherId, courseId = courseByOrg.id))
            } else {
                if (it.courseRepository.checkIfCourseNameExists(name = courseInfo.name)) {
                    return@run Either.Left(value = CourseServicesError.CourseNameAlreadyExists)
                }
            }
            val id = it.courseRepository.createCourse(course = CourseInput(orgUrl = courseInfo.orgUrl, name = courseInfo.name, teacherId = teacherId)).id
            val course = it.courseRepository.addTeacherToCourse(teacherId = teacherId, courseId = id)
            Either.Right(value = course)
        }
    }

    /**
     * Method that archives or deletes a course
     * If the course has classrooms, it archives it
     */
    fun archiveOrDeleteCourse(courseId: Int): CourseArchivedResponse {
        if (courseId <= 0) return Either.Left(value = CourseServicesError.CourseNotFound)
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
        if (courseId <= 0) return Either.Left(value = CourseServicesError.CourseNotFound)
        return transactionManager.run {
            if (it.courseRepository.getCourse(courseId = courseId) == null) return@run Either.Left(value = CourseServicesError.CourseNotFound)
            if (!it.courseRepository.isStudentInCourse(studentId = userId, courseId = courseId)) return@run Either.Left(value = CourseServicesError.UserNotInCourse)
            val course = it.courseRepository.leaveCourse(courseId = courseId, studentId = userId)
            Either.Right(value = course)
        }
    }

    fun getTeacherGithubToken(userId: Int): UserGithubTokenResponse {
        return transactionManager.run {
            val githubToken = it.usersRepository.getTeacherGithubToken(userId)
            if (githubToken == null) {
                Either.Left(value = CourseServicesError.NotTeacher)
            } else {
                Either.Right(AESDecrypt.decrypt(githubToken))
            }
        }
    }
}
