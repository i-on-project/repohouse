package com.isel.leic.ps.ion_classcode.http.controllers

import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.input.CourseInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseClassroomsOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseCreatedOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseOutputModel
import com.isel.leic.ps.ion_classcode.http.services.CourseServices
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.SirenModel
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class CourseController(
    private val courseServices: CourseServices,
) {

    @GetMapping(Uris.COURSE_PATH)
    fun getCourse(
        user: User,
        @PathVariable("id") id: Int,
    ): ResponseEntity<SirenModel<CourseOutputModel>> {
        return when (val course = courseServices.getCourseById(id)) {
            is Either.Left -> TODO()
            is Either.Right -> siren(value = CourseOutputModel(course.value.id, course.value.orgUrl, course.value.name, course.value.teacherId)) {
                clazz("course")
                link(rel = LinkRelation("self"), href = Uris.courseUri(id), needAuthentication = true)
                link(rel = LinkRelation("classroom"), href = Uris.courseClassroomsUri(id), needAuthentication = true)
                action(name = "Create Course", method = HttpMethod.POST, href = Uris.coursesUri(), type = "application/json", block = {
                    textField("orgUrl")
                    textField("name")
                    numberField("teacherId")
                })
            }
        }
    }

    @PostMapping(Uris.COURSES_PATH)
    fun createCourse(
        user: User,
        courseInfo: CourseInputModel,
    ): ResponseEntity<SirenModel<CourseCreatedOutputModel>> {
        return when (val course = courseServices.createCourse(courseInfo)) {
            is Either.Left -> TODO()
            is Either.Right -> siren(value = CourseCreatedOutputModel(id = course.value)) {
                clazz("course")
                link(rel = LinkRelation("self"), href = Uris.coursesUri(), needAuthentication = true)
                link(rel = LinkRelation("course"), href = Uris.courseUri(course.value), needAuthentication = true)
                link(rel = LinkRelation("classroom"), href = Uris.courseClassroomsUri(course.value), needAuthentication = true)
            }
        }
    }

    @GetMapping(Uris.COURSE_CLASSROOM_PATH)
    fun getCourseClassrooms(
        user: User,
        @PathVariable("id") id: Int,
    ): ResponseEntity<SirenModel<CourseClassroomsOutputModel>> {
        return when (val classrooms = courseServices.getCourseClassrooms(id)) {
            is Either.Left -> TODO()
            is Either.Right -> siren(value = CourseClassroomsOutputModel(classrooms.value)) {
                clazz("course")
                link(rel = LinkRelation("self"), href = Uris.courseClassroomsUri(id), needAuthentication = true)
                link(rel = LinkRelation("course"), href = Uris.courseUri(id), needAuthentication = true)
                action(name = "Create Course", method = HttpMethod.POST, href = Uris.coursesUri(), type = "application/json", block = {
                    textField("orgUrl")
                    textField("name")
                    numberField("teacherId")
                })
            }
        }
    }
}
