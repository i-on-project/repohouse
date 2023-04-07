package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.input.ClassroomInputModel
import com.isel.leic.ps.ion_classcode.http.model.input.ClassroomUpdateInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomArchivedModel
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomDeletedOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.DeliveryOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.http.services.ClassroomServices
import com.isel.leic.ps.ion_classcode.http.services.ClassroomServicesError
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ClassroomController(
    private val classroomServices: ClassroomServices
) {

    @GetMapping(Uris.CLASSROOM_PATH, produces = ["application/vnd.siren+json"])
    fun getClassroom(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable classroomId: Int,
    ): ResponseEntity<*> {
        return when (val classroom = classroomServices.getClassroom(classroomId)) {
            is Either.Left -> problem(classroom.value)
            is Either.Right -> siren(value = ClassroomOutputModel(id = classroom.value.id, name = classroom.value.name, isArchived = classroom.value.isArchived, lastSync = classroom.value.lastSync, assigments = classroom.value.assigments, students = classroom.value.students)) {
                clazz("classroom")
                link(rel = LinkRelation("self"), href = Uris.classroomUri(courseId, classroomId), needAuthentication = true)
                classroom.value.assigments.forEach {
                    link(rel = LinkRelation("assigment"), href = Uris.assigmentUri(courseId, classroomId, it.id), needAuthentication = true)
                }
                if (user is Teacher && !classroom.value.isArchived) {
                    link(rel = LinkRelation("local-copy"), href = Uris.localCopyUri(courseId, classroomId), needAuthentication = true)
                    action(name = "create-assigment", href = Uris.createAssigmentUri(courseId, classroomId), method = HttpMethod.POST, type = "application/json", block = {})
                    action(name = "edit-classroom", href = Uris.editClassroomUri(courseId, classroomId), method = HttpMethod.POST, type = "application/json", block = {})
                    action(name = "sync-classroom", href = Uris.syncClassroomUri(courseId,classroomId), method = HttpMethod.POST, type = "application/json", block = {})
                    action(name = "archive-classroom", href = Uris.archiveClassroomUri(courseId,classroomId), method = HttpMethod.POST, type = "application/json", block = {})
                }
            }
        }
    }

    @PostMapping(Uris.CREATE_CLASSROOM_PATH, produces = ["application/vnd.siren+json"])
    fun createClassroom(
        user: User,
        @PathVariable courseId: Int,
        @RequestBody classroomInfo: ClassroomInputModel,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.unauthorized
        return when (val classroomId = classroomServices.createClassroom(ClassroomInput(name = classroomInfo.name, courseId = courseId, teacherId = user.id))) {
            is Either.Left -> problem(classroomId.value)
            is Either.Right -> when (val classroom = classroomServices.getClassroom(classroomId.value)) {
                is Either.Left -> problem(classroom.value)
                is Either.Right -> siren(value = ClassroomOutputModel(id = classroom.value.id, name = classroom.value.name, isArchived = classroom.value.isArchived, lastSync = classroom.value.lastSync, assigments = classroom.value.assigments, students = classroom.value.students)) {
                    clazz("classroom")
                    link(rel = LinkRelation("classroom"), href = Uris.classroomUri(courseId,classroomId.value), needAuthentication = true)
                }
            }
        }
    }

    @PostMapping(Uris.ARCHIVE_CLASSROOM_PATH, produces = ["application/vnd.siren+json"])
    fun archiveClassroom(
        user: User,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.unauthorized
        return when (val archive = classroomServices.archiveOrDeleteClassroom(classroomId)) {
            is Either.Left -> problem(archive.value)
            is Either.Right ->
                if (archive.value is ClassroomArchivedModel.ClassroomArchived) {
                    when (val classroom = classroomServices.getClassroom(classroomId)) {
                        is Either.Left -> problem(classroom.value)
                        is Either.Right -> siren(value = ClassroomOutputModel(id = classroom.value.id, name = classroom.value.name, isArchived = classroom.value.isArchived, lastSync = classroom.value.lastSync, assigments = classroom.value.assigments, students = classroom.value.students)) {
                            clazz("classroom")
                            link(rel = LinkRelation("classroom"), href = Uris.classroomUri(courseId,classroomId), needAuthentication = true)
                            link(rel = LinkRelation("course"), href = Uris.courseUri(courseId), needAuthentication = true)
                        }
                    }
                } else {
                    siren(value = ClassroomDeletedOutputModel(id = classroomId, deleted = true)) {
                        clazz("classroom-deleted")
                        link(rel = LinkRelation("course"), href = Uris.courseUri(courseId), needAuthentication = true)
                    }
                }
        }
    }

    @PostMapping(Uris.EDIT_CLASSROOM_PATH, produces = ["application/vnd.siren+json"])
    fun editClassroom(
        user: User,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
        @RequestBody classroomInfo: ClassroomUpdateInputModel,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.unauthorized
        return when (val classroom = classroomServices.editClassroom(classroomId, classroomInfo)) {
            is Either.Left -> problem(classroom.value)
            is Either.Right -> siren(value = ClassroomOutputModel(id = classroom.value.id, name = classroom.value.name, isArchived = classroom.value.isArchived, lastSync = classroom.value.lastSync, assigments = classroom.value.assigments, students = classroom.value.students)) {
                clazz("classroom")
                link(rel = LinkRelation("classroom"), href = Uris.classroomUri(courseId,classroomId), needAuthentication = true)
            }
        }
    }

    @PostMapping(Uris.INVITE_LINK_PATH, produces = ["application/vnd.siren+json"])
    fun inviteLink(
        user: User,
        @PathVariable courseId: Int,
        @PathVariable inviteLink: String
    ): ResponseEntity<*> {
        if (user !is Student) return Problem.unauthorized
        return when (val enter = classroomServices.enterClassroomWithInvite(inviteLink,user.id)) {
            is Either.Left -> problem(enter.value)
            is Either.Right -> siren(value = ClassroomOutputModel(id = enter.value.id, name = enter.value.name, isArchived = enter.value.isArchived, lastSync = enter.value.lastSync, assigments = enter.value.assigments, students = enter.value.students)) {
                clazz("classroom")
                link(rel = LinkRelation("classroom"), href = Uris.classroomUri(courseId,enter.value.id), needAuthentication = true)
            }
        }
    }


    @PostMapping(Uris.SYNC_CLASSROOM_PATH, produces = ["application/vnd.siren+json"])
    suspend fun syncClassroom(
        user: User,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.unauthorized
        return when (val sync = classroomServices.syncClassroom(classroomId,user.id,courseId)) {
            is Either.Left -> problem(sync.value)
            is Either.Right ->  when(val classroom = classroomServices.getClassroom(classroomId)){
                is Either.Left -> problem(classroom.value)
                is Either.Right -> siren(value = ClassroomOutputModel(id = classroom.value.id, name = classroom.value.name, isArchived = classroom.value.isArchived, lastSync = classroom.value.lastSync, assigments = classroom.value.assigments, students = classroom.value.students)) {
                    clazz("classroom")
                    link(rel = LinkRelation("classroom"), href = Uris.classroomUri(courseId,classroomId), needAuthentication = true)
                }
            }
        }
    }

    @GetMapping(Uris.LOCAL_COPY_PATH)
    fun localCopy(
        user: User,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
    ):ResponseEntity<*>{
        if (user !is Teacher) return Problem.notTeacher
        return when(val localCopy = classroomServices.localCopy(classroomId,"C:\\Users\\ricar\\OneDrive\\Documentos")){
            is Either.Left -> problem(localCopy.value)
            is Either.Right -> siren("oi"){
                clazz("classroom")
                link(rel = LinkRelation("classroom"), href = Uris.classroomUri(courseId,classroomId), needAuthentication = true)
            }
        }
    }

    private fun problem(error: ClassroomServicesError): ResponseEntity<*> {
        return when (error) {
            ClassroomServicesError.ClasroomNotFound -> Problem.notFound
            ClassroomServicesError.ClassroomArchived -> Problem.invalidOperation
            ClassroomServicesError.AlreadyInClassroom -> Problem.invalidOperation
        }
    }
}
