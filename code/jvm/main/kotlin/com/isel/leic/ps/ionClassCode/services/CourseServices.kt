package com.isel.leic.ps.ionClassCode.services

import com.isel.leic.ps.ionClassCode.domain.Course
import com.isel.leic.ps.ionClassCode.domain.CourseWithClassrooms
import com.isel.leic.ps.ionClassCode.domain.CourseWithLeaveCourseRequests
import com.isel.leic.ps.ionClassCode.domain.LeaveCourseRequest
import com.isel.leic.ps.ionClassCode.domain.input.CourseInput
import com.isel.leic.ps.ionClassCode.domain.input.UpdateLeaveCourseCompositeInput
import com.isel.leic.ps.ionClassCode.domain.input.request.CompositeInput
import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveCourseInput
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveCourse
import com.isel.leic.ps.ionClassCode.http.model.input.CourseInputModel
import com.isel.leic.ps.ionClassCode.http.model.output.CourseArchivedResult
import com.isel.leic.ps.ionClassCode.http.model.output.LeaveClassroomRequest
import com.isel.leic.ps.ionClassCode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ionClassCode.http.model.problem.Problem
import com.isel.leic.ps.ionClassCode.repository.transaction.TransactionManager
import com.isel.leic.ps.ionClassCode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias CourseResponse = Result<CourseServicesError, CourseWithClassrooms>
typealias CourseWithLeaveCourseRequestsResponse = Result<CourseServicesError, CourseWithLeaveCourseRequests>
typealias CourseCreatedResponse = Result<CourseServicesError, Course>
typealias CourseArchivedResponse = Result<CourseServicesError, CourseArchivedResult>
typealias LeaveCourseResponse = Result<CourseServicesError, LeaveCourse>
typealias LeaveCourseRequestResponse = Result<CourseServicesError, Boolean>

/**
 * Error codes for the services
 */
sealed class CourseServicesError {
    object CourseNotFound : CourseServicesError()
    object LeaveCourseCompositeNotFound : CourseServicesError()
    object UserNotInCourse : CourseServicesError()
    object NotTeacher : CourseServicesError()
    object InvalidInput : CourseServicesError()
    object CourseArchived : CourseServicesError()
    object CourseNameAlreadyExists : CourseServicesError()
    object InternalError : CourseServicesError()
    object TeacherInCourse : CourseServicesError()
}

/**
 * Services for the course
 */
@Component
class CourseServices(
    private val transactionManager: TransactionManager,
    private val classroomServices: ClassroomServices
) {

    /**
     * Method that gets a course
     */
    fun getCourseById(courseId: Int, userId: Int, student: Boolean): CourseResponse {
        if (courseId < 0 || userId < 0) return Result.Problem(CourseServicesError.InvalidInput)
        return transactionManager.run {
            if (it.usersRepository.getUserById(userId) == null) return@run Result.Problem(CourseServicesError.InternalError)
            val course = it.courseRepository.getCourse(courseId) ?: return@run Result.Problem(CourseServicesError.CourseNotFound)
            val classrooms = it.courseRepository.getCourseUserClassrooms(courseId, userId, student)
            val students = it.courseRepository.getStudentInCourse(courseId)
            return@run Result.Success(
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

    fun getCourseWithLeaveRequestById(courseId: Int, userId: Int, student: Boolean): CourseWithLeaveCourseRequestsResponse {
        return transactionManager.run {
            if (it.usersRepository.getUserById(userId) == null) return@run Result.Problem(CourseServicesError.InternalError)
            val course = it.courseRepository.getCourse(courseId) ?: return@run Result.Problem(CourseServicesError.CourseNotFound)
            val classrooms = it.courseRepository.getCourseUserClassrooms(courseId, userId, student)
            val students = it.courseRepository.getStudentInCourse(courseId)
            val getLeaveCourseRequests = it.leaveCourseRepository.getLeaveCourseRequestsByCourse(courseId = courseId)
            val requests = getLeaveCourseRequests.map { leaveCourse ->
                val leaveClassRoomRequests = it.leaveClassroomRepository.getLeaveClassroomRequestByCompositeId(composite = leaveCourse.composite).map { leaveClassroom ->
                    val teams = it.leaveTeamRepository.getLeaveTeamWithRepoNameRequestsFromClassroom(classroomId = leaveClassroom.classroomId, compositeId = leaveClassroom.composite)
                    LeaveClassroomRequest(leaveClassroom = leaveClassroom, leaveTeamRequests = teams)
                }
                LeaveCourseRequest(leaveCourse = leaveCourse, leaveClassRoomRequests = leaveClassRoomRequests)
            }
            val courseWithClassrooms = CourseWithClassrooms(id = course.id, orgUrl = course.orgUrl, name = course.name, orgId = course.orgId, teachers = course.teachers, isArchived = course.isArchived, students = students, classrooms = classrooms)
            return@run Result.Success(
                CourseWithLeaveCourseRequests(
                    course = courseWithClassrooms,
                    leaveCourseRequests = requests,
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
            if (it.usersRepository.getTeacher(teacherId) == null) return@run Result.Problem(CourseServicesError.InternalError)
            val courseByOrg = it.courseRepository.getCourseByOrg(courseInfo.orgUrl)
            if (courseByOrg != null) {
                val teachers = it.courseRepository.getCourseTeachers(courseByOrg.id)
                if (teachers.any { teacher -> teacher.id == teacherId }) return@run Result.Problem(CourseServicesError.TeacherInCourse)
                return@run Result.Success(it.courseRepository.addTeacherToCourse(teacherId, courseByOrg.id))
            } else if (it.courseRepository.checkIfCourseNameExists(courseInfo.name)) {
                return@run Result.Problem(CourseServicesError.CourseNameAlreadyExists)
            }
            val id = it.courseRepository.createCourse(CourseInput(courseInfo.orgUrl, courseInfo.name, courseInfo.orgId, teacherId)).id
            val course = it.courseRepository.addTeacherToCourse(teacherId, id)
            return@run Result.Success(course)
        }
    }

    /**
     * Method that archives or deletes a course
     * If the course has classrooms, it archives it
     */
    fun archiveOrDeleteCourse(courseId: Int): CourseArchivedResponse {
        return transactionManager.run {
            val course = it.courseRepository.getCourse(courseId) ?: return@run Result.Problem(CourseServicesError.CourseNotFound)
            if (course.isArchived) return@run Result.Problem(CourseServicesError.CourseArchived)
            val classrooms = it.courseRepository.getCourseAllClassrooms(courseId)
            if (classrooms.isNotEmpty()) {
                it.courseRepository.archiveCourse(courseId)
                return@run Result.Success(CourseArchivedResult.CourseArchived)
            } else {
                it.courseRepository.deleteCourse(courseId)
                return@run Result.Success(CourseArchivedResult.CourseDeleted)
            }
        }
    }

    /**
     * Method to request to leave a course
     */
    fun leaveCourse(courseId: Int, userId: Int, githubUsername: String): LeaveCourseResponse {
        return transactionManager.run {
            if (it.usersRepository.getStudent(userId) == null) return@run Result.Problem(CourseServicesError.InternalError)
            if (it.courseRepository.getCourse(courseId) == null) return@run Result.Problem(CourseServicesError.CourseNotFound)
            if (!it.courseRepository.isStudentInCourse(userId, courseId)) return@run Result.Problem(CourseServicesError.UserNotInCourse)
            val composite = it.compositeRepository.createCompositeRequest(CompositeInput(), userId)
            val courseRequest = it.leaveCourseRepository.createLeaveCourseRequest(request = LeaveCourseInput(courseId = courseId, githubUsername = githubUsername, composite = composite.id), creator = userId)
            val classrooms = it.courseRepository.getCourseAllClassrooms(courseId = courseId)
            classrooms.forEach { classroom ->
                classroomServices.leaveClassroom(classroomId = classroom.id, userId = userId, githubUsername = githubUsername, compositeId = composite.id)
            }
            return@run Result.Success(value = courseRequest)
        }
    }

    fun updateLeaveCourseComposite(courseId: Int, userId: Int, body: UpdateLeaveCourseCompositeInput): LeaveCourseRequestResponse {
        return transactionManager.run {
            if (it.courseRepository.getCourse(courseId) == null) return@run Result.Problem(CourseServicesError.CourseNotFound)
            it.compositeRepository.getCompositeRequestsById(body.composite.requestId) ?: return@run Result.Problem(CourseServicesError.LeaveCourseCompositeNotFound)
            it.requestRepository.changeStateRequest(id = body.leaveCourse.requestId, state = "Accepted")
            body.leaveClassrooms.forEach { classroom ->
                classroomServices.updateLeaveClassroomComposite(userId = userId, body = classroom)
            }
            it.compositeRepository.updateCompositeState(compositeId = body.composite.requestId)
            return@run Result.Success(value = true)
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
            CourseServicesError.TeacherInCourse -> Problem.teacherInCourse
            CourseServicesError.LeaveCourseCompositeNotFound -> Problem.notFound
        }
    }
}
