package com.isel.leic.ps.ionClassCode.http.controllers.mobile

import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.domain.input.UpdateLeaveCourseCompositeInput
import com.isel.leic.ps.ionClassCode.http.Uris
import com.isel.leic.ps.ionClassCode.http.model.output.CourseWithClassroomOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.CourseWithLeaveCourseRequestsOutputModel
import com.isel.leic.ps.ionClassCode.infra.LinkRelation
import com.isel.leic.ps.ionClassCode.infra.siren
import com.isel.leic.ps.ionClassCode.services.CourseServices
import com.isel.leic.ps.ionClassCode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
        return when (val course = courseServices.getCourseWithLeaveRequestById(courseId = courseId, userId = user.id, student = false)) {
            is Result.Problem -> courseServices.problem(error = course.value)
            is Result.Success -> {
                val courseRes = CourseWithClassroomOutputModel(id = course.value.course.id, orgUrl = course.value.course.orgUrl, name = course.value.course.name, teacher = course.value.course.teachers, isArchived = course.value.course.isArchived, classrooms = course.value.course.classrooms)
                siren(
                    CourseWithLeaveCourseRequestsOutputModel(
                        course = courseRes,
                        leaveCourseRequests = course.value.leaveCourseRequests,
                    ),
                ) {
                    clazz(value = "course")
                    link(rel = LinkRelation(value = "self"), href = Uris.MOBILE_COURSE_PATH, needAuthentication = true)
                }
            }
        }
    }

    @PostMapping(Uris.MOBILE_LEAVE_COURSE_PATH, produces = ["application/vnd.siren+json"])
    fun leaveCourse(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable userId: Int,
        @RequestBody body: UpdateLeaveCourseCompositeInput,
    ): ResponseEntity<*> {
        return when (val result = courseServices.updateLeaveCourseComposite(courseId = courseId, userId = userId, body = body)) {
            is Result.Problem -> courseServices.problem(error = result.value)
            is Result.Success -> {
                siren(value = result.value) {
                    clazz(value = "leaveCourse")
                    link(rel = LinkRelation(value = "self"), href = Uris.MOBILE_LEAVE_COURSE_PATH, needAuthentication = true)
                }
            }
        }
    }
}
