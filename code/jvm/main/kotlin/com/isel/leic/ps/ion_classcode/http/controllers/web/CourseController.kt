package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.GITHUB_API_BASE_URL
import com.isel.leic.ps.ion_classcode.http.GITHUB_USER_ORGS
import com.isel.leic.ps.ion_classcode.http.OkHttp
import com.isel.leic.ps.ion_classcode.http.Status
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.makeCallToList
import com.isel.leic.ps.ion_classcode.http.model.input.CourseInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseArchivedOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseCreatedOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseDeletedOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseWithClassroomOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubOrgsModel
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubOrgsOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.RequestOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.http.services.CourseServices
import com.isel.leic.ps.ion_classcode.http.services.CourseServicesError
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.utils.Either
import okhttp3.Request
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
    private val okHttp: OkHttp,
    private val courseServices: CourseServices,
) {

    /**
     * Create a course
     * It will get from GitHub organizations from the Teacher's GitHub account
     * If some of the organizations are already in the database, the Teacher is just added to the course
     */
    @GetMapping(Uris.ORGS_PATH, produces = ["application/vnd.siren+json"])
    suspend fun getTeacherOrgs(
        user: User
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.unauthorized
        return when (val token = courseServices.getTeacherGithubToken(user.id)) {
            is Either.Left -> problem(token.value)
            is Either.Right -> {
                val orgs = fetchTeacherOrgs(token.value)
                siren(GitHubOrgsOutputModel(orgs)) {
                    clazz("course")
                    action(
                        title = "createCourse",
                        method = HttpMethod.POST,
                        href = Uris.COURSE_PATH,
                        type = "application/json",
                        block = {
                            textField(name = "name")
                            textField(name = "orgUrl")
                            numberField(name = "id")
                        })
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
        if (user !is Teacher) return Problem.unauthorized
        return when (val course = courseServices.createCourse(courseInfo, user.id)) {
            is Either.Left -> problem(course.value)
            is Either.Right -> siren(value = CourseCreatedOutputModel(course.value)) {
                clazz("course")
                link(rel = LinkRelation("self"), href = Uris.courseUri(course.value.id), needAuthentication = true)
                link(rel = LinkRelation("course"), href = Uris.courseUri(course.value.id), needAuthentication = true)
                link(rel = LinkRelation("courses"), href = Uris.coursesUri(), needAuthentication = true)
                link(rel = LinkRelation("students"), href = Uris.courseStudentsUri(course.value.id), needAuthentication = true)
                action(title = "Enter Course", method = HttpMethod.PUT, href = Uris.enterCourse(course.value.id), type = "application/json", block = {})
                action(title = "Leave Course", method = HttpMethod.PUT, href = Uris.leaveCourse(course.value.id), type = "application/json", block = {})
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
        return when (val course = courseServices.getCourseById(courseId, user.id)) {
            is Either.Left -> problem(course.value)
            is Either.Right -> siren(value = CourseWithClassroomOutputModel(course.value.id, course.value.orgUrl, course.value.name, course.value.teachers, course.value.isArchived, course.value.classrooms)) {
                clazz("course")
                link(rel = LinkRelation("self"), href = Uris.courseUri(course.value.id), needAuthentication = true)
                course.value.classrooms.forEach {
                    link(rel = LinkRelation("classroom"), href = Uris.classroomUri(courseId, it.id), needAuthentication = true)
                }
                if (user is Teacher) {
                    action(title = "create-classroom", method = HttpMethod.POST, href = Uris.createClassroomUri(course.value.id), type = "x-www-form-urlencoded", block = {
                        textField(name = "name")
                    })
                    action(title = "Delete Course", method = HttpMethod.DELETE, href = Uris.courseUri(course.value.id), type = "application/json", block = {})
                }
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
        return when (val request = courseServices.leaveCourse(courseId, user.id)) {
            is Either.Left -> problem(request.value)
            is Either.Right -> siren(
                value = RequestOutputModel(
                    status = Status.CREATED,
                    id = request.value.id,
                    title = "Request to leave course created, waiting for teacher approval",
                ),
            ) {
                clazz("course")
                link(rel = LinkRelation("self"), href = Uris.leaveCourse(courseId), needAuthentication = true)
                link(rel = LinkRelation("course"), href = Uris.courseUri(courseId), needAuthentication = true)
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
            is Either.Left -> problem(CourseServicesError.CourseNotFound)
            is Either.Right ->
                if (archive.value is CourseArchivedOutputModel.CourseArchived) {
                    when (val course = courseServices.getCourseById(courseId, user.id)) {
                        is Either.Left -> problem(course.value)
                        is Either.Right -> siren(
                            value = CourseWithClassroomOutputModel(
                                course.value.id,
                                course.value.orgUrl,
                                course.value.name,
                                course.value.teachers,
                                course.value.isArchived,
                                course.value.classrooms,
                            ),
                        ) {
                            clazz("course")
                            link(
                                rel = LinkRelation("self"),
                                href = Uris.courseUri(course.value.id),
                                needAuthentication = true,
                            )
                            course.value.classrooms.forEach {
                                link(
                                    rel = LinkRelation("classroom"),
                                    href = Uris.classroomUri(courseId, it.id),
                                    needAuthentication = true,
                                )
                            }
                            action(
                                title = "create-classroom",
                                method = HttpMethod.POST,
                                href = Uris.createClassroomUri(course.value.id),
                                type = "x-www-form-urlencoded",
                                block = {
                                    textField(name = "name")
                                },
                            )
                            action(
                                title = "Delete Course",
                                method = HttpMethod.DELETE,
                                href = Uris.courseUri(course.value.id),
                                type = "application/json",
                                block = {},
                            )
                        }
                    }
                } else {
                    siren(value = CourseDeletedOutputModel(id = courseId, deleted = true)) {
                        clazz("course-deleted")
                        link(rel = LinkRelation("menu"), href = Uris.menuUri(), needAuthentication = true)
                    }
                }
        }
    }

    /**
     * Method to fetch the teacher orgs from GitHub.
     */
    private suspend fun fetchTeacherOrgs(githubToken: String): List<GitHubOrgsModel> {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL$GITHUB_USER_ORGS")
            .addHeader("Authorization", "Bearer $githubToken")
            .addHeader("Accept", "application/vnd.github+json")
            .build()

        return okHttp.makeCallToList(request)
    }

    /**
     * Function to handle the errors
     */
    private fun problem(error: CourseServicesError): ResponseEntity<ErrorMessageModel> {
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
        }
    }
}
