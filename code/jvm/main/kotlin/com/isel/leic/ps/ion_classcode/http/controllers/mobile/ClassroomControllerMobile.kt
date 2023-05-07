package com.isel.leic.ps.ion_classcode.http.controllers.mobile

import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomOutputModel
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.services.ClassroomServices
import com.isel.leic.ps.ion_classcode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
        return when (val classroom = classroomServices.getClassroom(classroomId = classroomId)) {
            is Result.Problem -> classroomServices.problem(error = classroom.value)
            is Result.Success -> siren(
                ClassroomOutputModel(
                    id = classroom.value.id,
                    name = classroom.value.name,
                    isArchived = classroom.value.isArchived,
                    lastSync = classroom.value.lastSync,
                    assignments = classroom.value.assignments,
                    students = classroom.value.students,
                ),
            ) {
                clazz("classroom")
                link(rel = LinkRelation("self"), href = Uris.classroomUri(courseId = courseId, classroomId = classroomId), needAuthentication = true)
            }
        }
    }
}
