package com.isel.leic.ps.ion_classcode.http.controllers.mobile

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.output.CourseWithClassroomOutputModel
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.services.CourseServices
import com.isel.leic.ps.ion_classcode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CourseControllerMobile(
    private val courseServices: CourseServices
) {
    /**
     * Get all information about the course
     */
    @GetMapping(Uris.MOBILE_COURSE_PATH, produces = ["application/vnd.siren+json"])
    fun getCourse(
        user: User,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        return when (val course = courseServices.getCourseById(courseId = courseId, userId = user.id, student = false)) {
            is Result.Problem -> courseServices.problem(course.value)
            is Result.Success -> siren(CourseWithClassroomOutputModel(course.value.id, course.value.orgUrl, course.value.name, course.value.teachers, course.value.isArchived, course.value.classrooms)) {
                clazz("course")
                link(rel = LinkRelation("self"), href = Uris.courseUri(course.value.id), needAuthentication = true)
            }
        }
    }
}