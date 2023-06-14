package com.isel.leic.ps.ionClassCode.http.controllers.web

import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.Teacher
import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.http.Status
import com.isel.leic.ps.ionClassCode.http.Uris
import com.isel.leic.ps.ionClassCode.http.model.input.CourseInputModel
import com.isel.leic.ps.ionClassCode.http.model.output.CourseArchivedResult
import com.isel.leic.ps.ionClassCode.http.model.output.CourseCreatedOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.CourseDeletedOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.CourseWithClassroomOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.GitHubOrgsOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.RequestOutputModel
import com.isel.leic.ps.ionClassCode.http.model.problem.Problem
import com.isel.leic.ps.ionClassCode.infra.LinkRelation
import com.isel.leic.ps.ionClassCode.infra.siren
import com.isel.leic.ps.ionClassCode.services.CourseServices
import com.isel.leic.ps.ionClassCode.services.TeacherServices
import com.isel.leic.ps.ionClassCode.utils.Result
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Course Controller
 * All the write operations are only for Teachers
 */
@RestController
class CourseController(
    private val courseServices: CourseServices,
    private val teacherServices: TeacherServices,
) {

    /**
     * Create a course
     * It will get from GitHub organizations from the Teacher's GitHub account
     * If some organizations are already in the database, the Teacher is just added to the course
     */
    @GetMapping(Uris.ORGS_PATH, produces = ["application/vnd.siren+json"])
    suspend fun getTeacherOrgs(
        user: User,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val token = teacherServices.getTeacherGithubToken(user.id)) {
            is Result.Problem -> teacherServices.problem(token.value)
            is Result.Success -> {
                when (val orgs = teacherServices.getTeacherOrgs(user.id, user.githubUsername, token.value)) {
                    is Result.Problem -> teacherServices.problem(orgs.value)
                    is Result.Success -> siren(GitHubOrgsOutputModel(orgs.value)) {
                        clazz("orgs")
                        link(rel = LinkRelation("self"), href = Uris.ORGS_PATH, needAuthentication = true)
                    }
                }
            }
        }
    }

    /**
     * Create a course
     * It will get from GitHub organizations from the Teacher's GitHub account
     * If some organizations are already in the database, the Teacher is just added to the course
     */
    @PostMapping(Uris.COURSES_PATH, produces = ["application/vnd.siren+json"])
    fun createCourse(
        user: User,
        @RequestBody courseInfo: CourseInputModel,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val course = courseServices.createCourse(courseInfo, user.id)) {
            is Result.Problem -> courseServices.problem(course.value)
            is Result.Success -> siren(CourseCreatedOutputModel(course.value)) {
                clazz("course")
                action(title = "createCourse", href = Uris.COURSES_PATH, method = HttpMethod.POST, type = "application/json", block = {
                    textField("orgUrl")
                    textField("name")
                })
            }
        }
    }

    /**
     * Get all information about the course
     */
    @GetMapping(Uris.COURSE_PATH, produces = ["application/vnd.siren+json"])
    fun getCourse(
        user: User,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        return when (val course = courseServices.getCourseById(courseId, user.id, user is Student)) {
            is Result.Problem -> courseServices.problem(course.value)
            is Result.Success -> siren(CourseWithClassroomOutputModel(course.value.id, course.value.orgUrl, course.value.name, course.value.teachers, course.value.isArchived, course.value.classrooms)) {
                clazz("course")
                link(rel = LinkRelation("self"), href = Uris.courseUri(course.value.id), needAuthentication = true)
            }
        }
    }

    /**
     * Leave a course
     * Only Student
     * It will create a request to be the teacher to approve it
     */
    @PutMapping(Uris.LEAVE_COURSE_PATH, produces = ["application/vnd.siren+json"])
    fun leaveCourse(
        user: User,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        if (user !is Student) return Problem.unauthorized
        return when (val request = courseServices.leaveCourse(courseId, user.id, user.githubUsername)) {
            is Result.Problem -> courseServices.problem(request.value)
            is Result.Success -> siren(
                RequestOutputModel(
                    Status.CREATED,
                    request.value.id,
                    "Request to leave course created, waiting for teacher approval",
                ),
            ) {
                clazz("course")
                action(title = "leaveCourse", href = Uris.leaveCourse(courseId), method = HttpMethod.PUT, type = "application/json", block = {})
            }
        }
    }

    /**
     * Archive a course
     * It will archive the course and all the classrooms included in it
     */
    @PutMapping(Uris.COURSE_PATH, produces = ["application/vnd.siren+json"])
    fun archiveCourse(
        user: User,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.unauthorized
        return when (val archive = courseServices.archiveOrDeleteCourse(courseId)) {
            is Result.Problem -> courseServices.problem(archive.value)
            is Result.Success ->
                if (archive.value is CourseArchivedResult.CourseArchived) {
                    when (val course = courseServices.getCourseById(courseId, user.id, false)) {
                        is Result.Problem -> courseServices.problem(course.value)
                        is Result.Success -> siren(
                            CourseWithClassroomOutputModel(
                                course.value.id,
                                course.value.orgUrl,
                                course.value.name,
                                course.value.teachers,
                                course.value.isArchived,
                                course.value.classrooms,
                            ),
                        ) {
                            clazz("course")
                            action(title = "archiveCourse", href = Uris.courseUri(courseId), method = HttpMethod.PUT, type = "application/json", block = {})
                        }
                    }
                } else {
                    siren(CourseDeletedOutputModel(courseId, true)) {
                        clazz("course")
                        action(title = "archiveCourse", href = Uris.courseUri(courseId), method = HttpMethod.PUT, type = "application/json", block = {})
                    }
                }
        }
    }
}
