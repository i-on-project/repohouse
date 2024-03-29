package com.isel.leic.ps.ionClassCode.http.controllers.web

import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.Teacher
import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.domain.input.ClassroomInput
import com.isel.leic.ps.ionClassCode.http.Status
import com.isel.leic.ps.ionClassCode.http.Uris
import com.isel.leic.ps.ionClassCode.http.model.input.ClassroomInputModel
import com.isel.leic.ps.ionClassCode.http.model.input.ClassroomUpdateInputModel
import com.isel.leic.ps.ionClassCode.http.model.output.ClassroomArchivedOrDeletedOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.ClassroomArchivedResult
import com.isel.leic.ps.ionClassCode.http.model.output.ClassroomInviteOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.ClassroomOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.RequestOutputModel
import com.isel.leic.ps.ionClassCode.http.model.problem.Problem
import com.isel.leic.ps.ionClassCode.infra.LinkRelation
import com.isel.leic.ps.ionClassCode.infra.siren
import com.isel.leic.ps.ionClassCode.services.ClassroomServices
import com.isel.leic.ps.ionClassCode.utils.Result
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Classroom Controller
 * All the write operations are done by the teacher
 */
@RestController
class ClassroomController(
    private val classroomServices: ClassroomServices,
) {

    /**
     * Get all classroom information
     */
    @GetMapping(Uris.CLASSROOM_PATH, produces = ["application/vnd.siren+json"])
    fun getClassroom(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
    ): ResponseEntity<*> {
        return when (val classroom = classroomServices.getClassroom(classroomId)) {
            is Result.Problem -> classroomServices.problem(classroom.value)
            is Result.Success -> siren(
                ClassroomOutputModel(
                    id = classroom.value.id,
                    name = classroom.value.name,
                    isArchived = classroom.value.isArchived,
                    lastSync = classroom.value.lastSync,
                    inviteCode = classroom.value.inviteCode,
                    assignments = classroom.value.assignments,
                    students = classroom.value.students,
                ),
            ) {
                clazz("classroom")
                link(rel = LinkRelation("self"), href = Uris.classroomUri(courseId, classroomId), needAuthentication = true)
            }
        }
    }

    /**
     * Create a new classroom
     */
    @PostMapping(Uris.CREATE_CLASSROOM_PATH, produces = ["application/vnd.siren+json"])
    fun createClassroom(
        user: User,
        @PathVariable courseId: Int,
        @RequestBody classroomInfo: ClassroomInputModel,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val classroom = classroomServices.createClassroom(ClassroomInput(classroomInfo.name, courseId, user.id))) {
            is Result.Problem -> classroomServices.problem(classroom.value)
            is Result.Success -> siren(
                ClassroomOutputModel(
                    id = classroom.value.id,
                    name = classroom.value.name,
                    isArchived = classroom.value.isArchived,
                    lastSync = classroom.value.lastSync,
                    inviteCode = classroom.value.inviteCode,
                    assignments = classroom.value.assignments,
                    students = classroom.value.students,
                ),
            ) {
                clazz("classroom")
                link(rel = LinkRelation("self"), href = Uris.classroomUri(courseId, classroom.value.id), needAuthentication = true)
            }
        }
    }

    /**
     * Archive a classroom
     * Possible if no assignments are created
     */
    @PutMapping(Uris.ARCHIVE_CLASSROOM_PATH, produces = ["application/vnd.siren+json"])
    fun archiveClassroom(
        user: User,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val archive = classroomServices.archiveOrDeleteClassroom(classroomId = classroomId, creator = user.id)) {
            is Result.Problem -> classroomServices.problem(archive.value)
            is Result.Success ->
                if (archive.value is ClassroomArchivedResult.ClassroomArchived) {
                    when (val classroom = classroomServices.getClassroom(classroomId)) {
                        is Result.Problem -> classroomServices.problem(classroom.value)
                        is Result.Success -> siren(ClassroomArchivedOrDeletedOutputModel(classroomId, archived = true, deleted = false)) {
                            clazz("classroom")
                        }
                    }
                } else {
                    siren(ClassroomArchivedOrDeletedOutputModel(classroomId, archived = false, deleted = true)) {
                        clazz("classroom")
                    }
                }
        }
    }

    /**
     * Edit a classroom
     * Only Teacher
     */
    @PutMapping(Uris.EDIT_CLASSROOM_PATH, produces = ["application/vnd.siren+json"])
    fun editClassroom(
        user: User,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
        @RequestBody classroomInfo: ClassroomUpdateInputModel,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val classroom = classroomServices.editClassroom(classroomId, classroomInfo)) {
            is Result.Problem -> classroomServices.problem(classroom.value)
            is Result.Success -> siren(
                ClassroomOutputModel(
                    id = classroom.value.id,
                    name = classroom.value.name,
                    isArchived = classroom.value.isArchived,
                    lastSync = classroom.value.lastSync,
                    inviteCode = classroom.value.inviteCode,
                    assignments = classroom.value.assignments,
                    students = classroom.value.students,
                ),
            ) {
                clazz("classroom")
                action(title = "editClassroom", href = Uris.editClassroomUri(courseId, classroomId), method = HttpMethod.PUT, type = "application/json", block = {
                    textField("name")
                })
            }
        }
    }

    /**
     * Enter a classroom with an invitation link
     */
    @PostMapping(Uris.INVITE_LINK_PATH, produces = ["application/vnd.siren+json"])
    fun inviteLink(
        user: User,
        @PathVariable inviteLink: String,
    ): ResponseEntity<*> {
        if (user !is Student) return Problem.notStudent
        return when (val enter = classroomServices.enterClassroomWithInvite(inviteLink, user.id)) {
            is Result.Problem -> classroomServices.problem(enter.value)
            is Result.Success -> siren(
                ClassroomInviteOutputModel(
                    courseId = enter.value.courseId,
                    classroom = enter.value.classroom,
                ),
            ) {
                clazz("classroom")
            }
        }
    }

    /**
     * Sync a classroom with the GitHub truth
     */
    @PutMapping(Uris.SYNC_CLASSROOM_PATH, produces = ["application/vnd.siren+json"])
    suspend fun syncClassroom(
        user: User,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val sync = classroomServices.syncClassroom(classroomId, user.id, courseId)) {
            is Result.Problem -> classroomServices.problem(sync.value)
            is Result.Success -> when (val classroom = classroomServices.getClassroom(classroomId)) {
                is Result.Problem -> classroomServices.problem(classroom.value)
                is Result.Success -> siren(
                    ClassroomOutputModel(
                        id = classroom.value.id,
                        name = classroom.value.name,
                        isArchived = classroom.value.isArchived,
                        lastSync = classroom.value.lastSync,
                        inviteCode = classroom.value.inviteCode,
                        assignments = classroom.value.assignments,
                        students = classroom.value.students,
                    ),
                ) {
                    clazz("classroom")
                    action(title = "syncClassroom", href = Uris.syncClassroomUri(courseId, classroomId), method = HttpMethod.PUT, type = "application/json", block = {})
                }
            }
        }
    }

    /**
     * Leave a classroom
     * Only Student
     * It will create a request to be the teacher to approve it
     */
    @PutMapping(Uris.LEAVE_CLASSROOM_PATH, produces = ["application/vnd.siren+json"])
    fun leaveClassroom(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int
    ): ResponseEntity<*> {
        if (user !is Student) return Problem.unauthorized
        return when (val request = classroomServices.leaveClassroom(classroomId, user.id, user.githubUsername)) {
            is Result.Problem -> classroomServices.problem(request.value)
            is Result.Success -> siren(
                RequestOutputModel(
                    Status.CREATED,
                    request.value.id,
                    "Request to leave classroom created, waiting for teacher approval",
                ),
            ) {
                clazz("classroom")
                action(title = "leaveClassroom", href = Uris.leaveClassroomUri(courseId, classroomId), method = HttpMethod.PUT, type = "application/json", block = {})
            }
        }
    }

    /**
     * Create a local copy of a classroom to the personal computer
     * Only Teacher
     */
    @GetMapping(Uris.LOCAL_COPY_PATH)
    fun localCopy(
        user: User,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val localCopy = classroomServices.localCopy(classroomId)) {
            is Result.Problem -> classroomServices.problem(localCopy.value)
            is Result.Success ->
                ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${localCopy.value.fileName}\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(localCopy.value.file.length())
                    .body(InputStreamResource(localCopy.value.file.inputStream()))
        }
    }
}
