package com.isel.leic.ps.ion_classcode.http.controllers

import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.Status
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.input.CourseInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseCreatedOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseOutputModel
import com.isel.leic.ps.ion_classcode.http.services.ClassroomServices
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
    private val classroomServices: ClassroomServices
) {

    @GetMapping(Uris.COURSE_PATH)
    fun getCourse(
        user: User,
        @PathVariable("id") id: Int,
    ): ResponseEntity<SirenModel<CourseOutputModel>> {
        return if (user is Teacher) {
            getCoursesTeacher(user, id)
        } else {
            getCoursesStudent(user, id)
        }
    }

    private fun getCoursesTeacher(user: User,id: Int): ResponseEntity<SirenModel<CourseOutputModel>> {
        return when (val course = courseServices.getCourseById(id)) {
            is Either.Left -> TODO()
            is Either.Right -> siren(value = CourseOutputModel(course.value.id, course.value.orgUrl, course.value.name, course.value.teacherId,course.value.classrooms)) {
                clazz("course")
                link(rel = LinkRelation("self"), href = Uris.courseUri(id), needAuthentication = true)
                course.value.classrooms.forEach {
                    link(rel = LinkRelation("classroom"), href = Uris.classroomUri(it.id), needAuthentication = true)
                }
                action(name = "Create Course", method = HttpMethod.POST, href = Uris.coursesUri(), type = "application/json", block = {
                    textField("orgUrl")
                    textField("name")
                    numberField("teacherId")
                })
            }
        }
    }

    private fun getCoursesStudent(user: User,courseId: Int): ResponseEntity<SirenModel<CourseOutputModel>> {
        return when (val classroomId = classroomServices.getStudentClassroom(courseId,user.id)) {
            is Either.Left -> TODO()
            is Either.Right -> ResponseEntity
                .status(Status.REDIRECT)
                .header("Location", Uris.classroomUri(classroomId.value).toString())
                .build()
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
            }
        }
    }

}
