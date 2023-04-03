package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.Status
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.input.CourseInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseCreatedOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseWithClassroomOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CoursesOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.RequestOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.http.services.CourseServices
import com.isel.leic.ps.ion_classcode.http.services.CourseServicesError
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CourseController(
    private val courseServices: CourseServices,
) {

    @PostMapping(Uris.COURSES_PATH, produces = ["application/vnd.siren+json"])
    fun createCourse(
        user: User,
        @RequestBody courseInfo: CourseInputModel,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.unauthorized
        // TODO: Check if course already exists, adds teacher to course
        return when (val course = courseServices.createCourse(courseInfo)) {
            is Either.Left -> problem(course.value)
            is Either.Right -> siren(value = CourseCreatedOutputModel(id = course.value)) {
                clazz("course")
                link(rel = LinkRelation("self"), href = Uris.courseUri(course.value), needAuthentication = true)
                link(rel = LinkRelation("course"), href = Uris.courseUri(course.value), needAuthentication = true)
                link(rel = LinkRelation("courses"), href = Uris.coursesUri(), needAuthentication = true)
                link(rel = LinkRelation("students"), href = Uris.courseStudentsUri(course.value), needAuthentication = true)
                action(name = "Enter Course", method = HttpMethod.PUT, href = Uris.enterCourse(course.value), type = "application/json", block = {})
                action(name = "Leave Course", method = HttpMethod.PUT, href = Uris.leaveCourse(course.value), type = "application/json", block = {})
            }
        }
    }

    @GetMapping(Uris.COURSE_PATH, produces = ["application/vnd.siren+json"])
    fun getCourse(
        user: User,
        @PathVariable("courseId") id: Int,
    ): ResponseEntity<*> {
        return when (val course = courseServices.getCourseById(id)) {
            is Either.Left -> problem(course.value)
            is Either.Right -> siren(value = CourseWithClassroomOutputModel(course.value.id, course.value.orgUrl, course.value.name, course.value.teacherId, course.value.classrooms)) {
                clazz("course")
                link(rel = LinkRelation("self"), href = Uris.courseUri(course.value.id), needAuthentication = true)
                course.value.classrooms.forEach {
                    link(rel = LinkRelation("classroom"), href = Uris.classroomUri(it.id), needAuthentication = true)
                }
                if (user is Teacher) {
                    action(name = "create-classroom", method = HttpMethod.POST, href = Uris.createClassroomUri(course.value.id), type = "x-www-form-urlencoded", block = {
                        textField(name = "name")
                    })
                    action(name = "Delete Course", method = HttpMethod.DELETE, href = Uris.courseUri(course.value.id), type = "application/json", block = {})
                }
            }
        }
    }

    // TODO: Check utility of this method
    @GetMapping(Uris.COURSES_PATH, produces = ["application/vnd.siren+json"])
    fun getUserCourses(
        user: User,
    ): ResponseEntity<*> {
        return when (val course = if (user is Teacher) courseServices.getTeacherCourses(user.id) else courseServices.getStudentCourses(user.id)) {
            is Either.Left -> problem(course.value)
            is Either.Right -> siren(value = CoursesOutputModel(course.value)) {
                clazz("course")
                link(rel = LinkRelation("self"), href = Uris.coursesUri(), needAuthentication = true)
                course.value.forEach {
                    link(rel = LinkRelation("classroom"), href = Uris.classroomUri(it.id), needAuthentication = true)
                }
            }
        }
    }

    @PutMapping(Uris.LEAVE_COURSE_PATH, produces = ["application/vnd.siren+json"])
    fun leaveCourse(
        user: User,
        @PathVariable("courseId") id: Int,
    ): ResponseEntity<*> {
        if (user !is Student) return Problem.unauthorized
        return when (val request = courseServices.leaveCourse(id, user.id)) {
            is Either.Left -> problem(request.value)
            is Either.Right -> siren(value = RequestOutputModel(status = Status.CREATED, id = request.value, title = "Request to leave course created, waiting for teacher approval")) {
                clazz("course")
                link(rel = LinkRelation("self"), href = Uris.leaveCourse(id), needAuthentication = true)
                link(rel = LinkRelation("course"), href = Uris.courseUri(id), needAuthentication = true)
            }
        }
    }

    @DeleteMapping(Uris.COURSE_PATH, produces = ["application/vnd.siren+json"])
    fun deleteCourse(
        user: User,
        @PathVariable("courseId") id: Int,
    ) {
        // TODO: CHECK IF THIS IS APLIABLE
    }

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
        }
    }
}
