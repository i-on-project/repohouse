package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.CourseWithClassrooms
import com.isel.leic.ps.ion_classcode.domain.CourseWithStudents
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.http.model.input.CourseInputModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias CourseResponse = Either<CourseServicesError, CourseWithClassrooms>
typealias CourseCreatedResponse = Either<CourseServicesError, Course>
typealias UserCoursesResponse = Either<CourseServicesError, List<CourseWithClassrooms>>
typealias CourseStudentsResponse = Either<CourseServicesError, CourseWithStudents>
typealias EnterCourseResponse = Either<CourseServicesError, Course>
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
            if (course == null) {
                Either.Left(CourseServicesError.CourseNotFound)
            } else {
                Either.Right(CourseWithClassrooms(course.id, course.orgUrl, course.name, course.teacherId, classrooms))
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
                courses.map { course -> CourseWithClassrooms(course.id, course.orgUrl, course.name, course.teacherId, it.courseRepository.getCourseClassrooms(course.id)) }
            )
        }
    }

    fun getStudentCourses(userId: Int): UserCoursesResponse {
        return transactionManager.run {
            if (it.usersRepository.getUserById(userId) == null) Either.Left(CourseServicesError.UserNotFound)
            if (it.usersRepository.getStudent(userId) == null) Either.Left(CourseServicesError.NotStudent)
            val courses = it.courseRepository.getAllStudentCourses(userId)
            Either.Right(
                courses.map { course -> CourseWithClassrooms(course.id, course.orgUrl, course.name, course.teacherId, it.courseRepository.getCourseClassrooms(course.id)) }
            )
        }
    }

    fun getStudentsInCourse(courseId: Int): CourseStudentsResponse {
        return transactionManager.run {
            val course = it.courseRepository.getCourse(courseId)
            if (course == null) {
                Either.Left(CourseServicesError.CourseNotFound)
            } else {
                val students = it.courseRepository.getStudentInCourse(courseId)
                Either.Right(CourseWithStudents(course.id, course.orgUrl, course.name, course.teacherId, students))
            }
        }
    }

    fun enterCourse(courseId: Int, userId: Int): EnterCourseResponse {
        return transactionManager.run {
            if (it.courseRepository.getCourse(courseId) == null) Either.Left(CourseServicesError.CourseNotFound)
            if (it.usersRepository.getUserById(userId) == null) Either.Left(CourseServicesError.UserNotFound)
            if (it.courseRepository.isUserInCourse(userId, courseId)) Either.Left(CourseServicesError.UserInCourse)
            val course = it.courseRepository.enterCourse(courseId, userId)
            Either.Right(course)
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
