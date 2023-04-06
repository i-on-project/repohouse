package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Course
import com.isel.leic.ps.ion_classcode.domain.CourseWithClassrooms
import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.http.model.input.CourseInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseArchivedModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias CourseResponse = Either<CourseServicesError, CourseWithClassrooms>
typealias CourseCreatedResponse = Either<CourseServicesError, Course>
typealias UserCoursesResponse = Either<CourseServicesError, List<CourseWithClassrooms>>
typealias CourseArchivedResponse = Either<CourseServicesError, CourseArchivedModel>
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
    object CourseArchived : CourseServicesError()
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
                val teachers = it.courseRepository.getCourseTeachers(courseId)
                Either.Right(CourseWithClassrooms(course.id, course.orgUrl, course.name, teachers, course.isArchived, students, classrooms))
            }
        }
    }

    fun createCourse(courseInfo: CourseInputModel): CourseCreatedResponse {
        return transactionManager.run {
            if (it.usersRepository.getTeacher(courseInfo.teacherId) == null) Either.Left(CourseServicesError.NotTeacher)
            if (courseInfo.orgUrl.isEmpty() || courseInfo.name.isEmpty()) Either.Left(CourseServicesError.InvalidInput)
            val courseOrg = it.courseRepository.getCourseByOrg(courseInfo.orgUrl)
            val id = if (courseOrg != null) {
                it.courseRepository.addTeacherToCourse(courseInfo.teacherId, courseInfo.orgUrl)
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

    fun getTeacherCourses(userId: Int): UserCoursesResponse {
        return transactionManager.run {
            if (it.usersRepository.getUserById(userId) == null) Either.Left(CourseServicesError.UserNotFound)
            if (it.usersRepository.getTeacher(userId) == null) Either.Left(CourseServicesError.NotTeacher)
            val courses = it.courseRepository.getAllTeacherCourses(userId)
            Either.Right(
                courses.map { course ->
                    val teachers = it.courseRepository.getCourseTeachers(course.id)
                    CourseWithClassrooms(course.id, course.orgUrl, course.name, teachers, course.isArchived)
                }, // Empty list of students and classrooms because info is not needed
            )
        }
    }

    fun getStudentCourses(userId: Int): UserCoursesResponse {
        return transactionManager.run {
            if (it.usersRepository.getUserById(userId) == null) Either.Left(CourseServicesError.UserNotFound)
            if (it.usersRepository.getStudent(userId) == null) Either.Left(CourseServicesError.NotStudent)
            val courses = it.courseRepository.getAllStudentCourses(userId)
            Either.Right(
                courses.map { course ->
                    val teachers = it.courseRepository.getCourseTeachers(course.id)
                    CourseWithClassrooms(course.id, course.orgUrl, course.name, teachers)
                }, // Empty list of students and classrooms because info is not needed
            )
        }
    }

    fun archiveOrDeleteCourse(courseId: Int): CourseArchivedResponse {
        return transactionManager.run {
            if (it.courseRepository.getCourse(courseId) == null) Either.Left(CourseServicesError.CourseNotFound)
            val course = it.courseRepository.getCourse(courseId)
            if (course == null) {
                Either.Left(CourseServicesError.CourseNotFound)
            } else if (course.isArchived) Either.Left(CourseServicesError.CourseArchived)
            val classrooms = it.courseRepository.getCourseClassrooms(courseId)
            if (classrooms.isNotEmpty()) {
                it.courseRepository.archiveCourse(courseId)
                Either.Right(CourseArchivedModel.CourseArchived)
            } else {
                it.courseRepository.deleteCourse(courseId)
                Either.Right(CourseArchivedModel.CourseDeleted)
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
