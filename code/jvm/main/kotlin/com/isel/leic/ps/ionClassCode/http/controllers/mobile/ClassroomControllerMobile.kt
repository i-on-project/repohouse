package com.isel.leic.ps.ionClassCode.http.controllers.mobile

import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.domain.input.UpdateArchiveRepoInput
import com.isel.leic.ps.ionClassCode.http.Uris
import com.isel.leic.ps.ionClassCode.infra.LinkRelation
import com.isel.leic.ps.ionClassCode.infra.siren
import com.isel.leic.ps.ionClassCode.services.ClassroomServices
import com.isel.leic.ps.ionClassCode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ClassroomControllerMobile(
    private val classroomServices: ClassroomServices,
) {

    /**
     * Get all classroom information
     */
    @GetMapping(Uris.MOBILE_CLASSROOM_PATH, produces = ["application/vnd.siren+json"])
    fun getClassroom(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
    ): ResponseEntity<*> {
        return when (val classroom = classroomServices.getClassroomWithArchiveRequest(classroomId = classroomId)) {
            is Result.Problem -> classroomServices.problem(error = classroom.value)
            is Result.Success -> siren(value = classroom.value) {
                clazz("classroom")
                link(rel = LinkRelation("self"), href = Uris.classroomUri(courseId = courseId, classroomId = classroomId), needAuthentication = true)
            }
        }
    }

    @PostMapping(Uris.MOBILE_CLASSROOM_ARCHIVED_PATH, produces = ["application/vnd.siren+json"])
    fun updateArchivedRequestsClassroom(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
        @RequestBody body: UpdateArchiveRepoInput,
    ): ResponseEntity<*> {
        return when (val classroom = classroomServices.updateRequestState(body = body, classroomId = classroomId)) {
            is Result.Problem -> classroomServices.problem(error = classroom.value)
            is Result.Success -> siren(value = classroom.value) {
                clazz("classroomArchiveRequest")
                link(rel = LinkRelation("self"), href = Uris.classroomUri(courseId = courseId, classroomId = classroomId), needAuthentication = true)
            }
        }
    }
}
