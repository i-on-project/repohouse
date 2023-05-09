package com.isel.leic.ps.ionClassCode.http.controllers.mobile

import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.http.Uris
import com.isel.leic.ps.ionClassCode.http.model.output.CourseWithClassroomOutputModel
import com.isel.leic.ps.ionClassCode.infra.LinkRelation
import com.isel.leic.ps.ionClassCode.infra.siren
import com.isel.leic.ps.ionClassCode.services.CourseServices
import com.isel.leic.ps.ionClassCode.utils.Result
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CourseControllerMobile(
    private val courseServices: CourseServices,
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
            is Result.Problem -> courseServices.problem(error = course.value)
            is Result.Success -> {
                log.info("course classrooms = ${course.value.classrooms}")
                siren(
                    CourseWithClassroomOutputModel(
                        id = course.value.id,
                        orgUrl = course.value.orgUrl,
                        name = course.value.name,
                        teacher = course.value.teachers,
                        isArchived = course.value.isArchived,
                        classrooms = course.value.classrooms,
                    ),
                ) {
                    clazz(value = "course")
                    link(rel = LinkRelation(value = "self"), href = Uris.courseUri(courseId = course.value.id), needAuthentication = true)
                }
            }
        }
    }
    companion object {
        private val log = LoggerFactory.getLogger(CourseControllerMobile::class.java)
    }
}
