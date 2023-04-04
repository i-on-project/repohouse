package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.CourseWithClassrooms
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.http.model.input.CourseInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseArchivedOutputModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias CourseResponse = Either<CourseServicesError, CourseWithClassrooms>
typealias CourseCreatedResponse = Either<CourseServicesError, Course>
typealias UserCoursesResponse = Either<CourseServicesError, List<CourseWithClassrooms>>
typealias CourseArchivedResponse = Either<CourseServicesError, CourseArchivedOutputModel>
typealias LeaveCourseResponse = Either<CourseServicesError, Course>

sealed class CourseServicesError {
    object CourseNotFound : CourseServicesError()
    object CourseAlreadyExists : CourseServicesError()
    object UserInCourse : CourseServicesError()
    object UserNotInCourse : CourseServicesError()
    object UserNotFound : CourseServicesError()
    object NotStudent : CourseServicesError()
    object NotTeacher : CourseServicesError()
    object InvalidInput : CourseServicesError()
}

@Component
class CourseServices(
    private val transactionManager: TransactionManager,
) {

    fun getCourseById(courseId: Int): CourseResponse {
        return transactionManager.run {
            val course = it.courseRepository.getCourse(courseId)
            val classrooms = it.courseRepository.getCourseClassrooms(courseId)
            val students = it.courseRepository.getStudentInCourse(courseId)
            if (course == null) {
                Either.Left(CourseServicesError.CourseNotFound)
            } else {
                Either.Right(CourseWithClassrooms(course.id, course.orgUrl, course.name, course.teacherId, students, classrooms))
            }
        }
    }

    fun createCourse(courseInfo: CourseInputModel): CourseCreatedResponse {
        return transactionManager.run {
            if (courseInfo.orgUrl.isEmpty() || courseInfo.name.isEmpty()) Either.Left(CourseServicesError.InvalidInput)
            if (it.courseRepository.getCourseByOrg(courseInfo.orgUrl) != null) Either.Left(CourseServicesError.CourseAlreadyExists)
            if (it.courseRepository.getCourseByName(courseInfo.name) != null) Either.Left(CourseServicesError.CourseAlreadyExists)
            val id = it.courseRepository.createCourse(CourseInput(courseInfo.orgUrl, courseInfo.name, courseInfo.teacherId))
            Either.Right(id)
        }
    }

    fun getTeacherCourses(userId: Int): UserCoursesResponse {
        return transactionManager.run {
            if (it.usersRepository.getUserById(userId) == null) Either.Left(CourseServicesError.UserNotFound)
            if (it.usersRepository.getTeacher(userId) == null) Either.Left(CourseServicesError.NotTeacher)
            val courses = it.courseRepository.getAllTeacherCourses(userId)
            Either.Right(
                courses.map { course -> CourseWithClassrooms(course.id, course.orgUrl, course.name, course.teacherId) }, // Empty list of students and classrooms because info is not needed
            )
        }
    }

    fun getStudentCourses(userId: Int): UserCoursesResponse {
        return transactionManager.run {
            if (it.usersRepository.getUserById(userId) == null) Either.Left(CourseServicesError.UserNotFound)
            if (it.usersRepository.getStudent(userId) == null) Either.Left(CourseServicesError.NotStudent)
            val courses = it.courseRepository.getAllStudentCourses(userId)
            Either.Right(
                courses.map { course -> CourseWithClassrooms(course.id, course.orgUrl, course.name, course.teacherId) }, // Empty list of students and classrooms because info is not needed
            )
        }
    }

    fun archiveOrDeleteCourse(courseId: Int): CourseArchivedResponse {
        return transactionManager.run {
            if (it.courseRepository.getCourse(courseId) == null) Either.Left(CourseServicesError.CourseNotFound)
            val classrooms = it.courseRepository.getCourseClassrooms(courseId)
            if (classrooms.isNotEmpty()) {
                it.courseRepository.archiveCourse(courseId)
                Either.Right(CourseArchivedOutputModel.CourseArchived)
            } else {
                it.courseRepository.deleteCourse(courseId)
                Either.Right(CourseArchivedOutputModel.CourseDeleted)
            }
        }
    }

    fun leaveCourse(courseId: Int, userId: Int): LeaveCourseResponse {
        return transactionManager.run {
            if (it.courseRepository.getCourse(courseId) == null) Either.Left(CourseServicesError.CourseNotFound)
            if (it.usersRepository.getUserById(userId) == null) Either.Left(CourseServicesError.UserNotFound)
            if (!it.courseRepository.isUserInCourse(userId, courseId)) Either.Left(CourseServicesError.UserNotInCourse)
            val course = it.courseRepository.leaveCourse(courseId, userId)
            Either.Right(course)
        }
    }
}
