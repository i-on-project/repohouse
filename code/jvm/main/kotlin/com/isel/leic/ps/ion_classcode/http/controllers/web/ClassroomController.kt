package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.input.ClassroomInputModel
import com.isel.leic.ps.ion_classcode.http.model.input.ClassroomUpdateInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomArchivedOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.http.services.AssigmentServices
import com.isel.leic.ps.ion_classcode.http.services.ClassroomServices
import com.isel.leic.ps.ion_classcode.http.services.ClassroomServicesError
import com.isel.leic.ps.ion_classcode.http.services.TeamServices
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
    private val classroomServices: ClassroomServices,
    private val teamServices: TeamServices
) {
    // TODO: syncClassroom : be the last thing to do

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
                link(rel = LinkRelation("self"), href = Uris.classroomUri(classroomId), needAuthentication = true)
                classroom.value.assigments.forEach {
                    link(rel = LinkRelation("assigment"), href = Uris.assigmentUri(it.id), needAuthentication = true)
                }
                if (user is Teacher && !classroom.value.isArchived) {
                    action(name = "create-assigment", href = Uris.createAssigmentUri(classroomId), method = HttpMethod.POST, type = "application/json", block = {})
                    action(name = "edit-classroom", href = Uris.editClassroomUri(classroomId), method = HttpMethod.POST, type = "application/json", block = {})
                    action(name = "sync-classroom", href = Uris.syncClassroomUri(classroomId), method = HttpMethod.POST, type = "application/json", block = {})
                    action(name = "archive-classroom", href = Uris.archiveClassroomUri(classroomId), method = HttpMethod.POST, type = "application/json", block = {})
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
        return when (val classroomId = classroomServices.createClassroom(ClassroomInput(name = classroomInfo.name, courseId = courseId))) {
            is Either.Left -> problem(classroomId.value)
            is Either.Right -> when (val classroom = classroomServices.getClassroom(classroomId.value)) {
                is Either.Left -> problem(classroom.value)
                is Either.Right -> siren(value = ClassroomOutputModel(id = classroom.value.id, name = classroom.value.name, isArchived = classroom.value.isArchived, lastSync = classroom.value.lastSync, assigments = classroom.value.assigments, students = classroom.value.students)) {
                    clazz("classroom")
                    link(rel = LinkRelation("classroom"), href = Uris.classroomUri(classroomId.value), needAuthentication = true)
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
                if (archive.value is ClassroomArchivedOutputModel.ClassroomArchived) {
                    when (val classroom = classroomServices.getClassroom(classroomId)) {
                        is Either.Left -> problem(classroom.value)
                        is Either.Right -> siren(value = ClassroomOutputModel(id = classroom.value.id, name = classroom.value.name, isArchived = classroom.value.isArchived, lastSync = classroom.value.lastSync, assigments = classroom.value.assigments, students = classroom.value.students)) {
                            clazz("classroom")
                            link(rel = LinkRelation("classroom"), href = Uris.classroomUri(classroomId), needAuthentication = true)
                        }
                    }
                } else {
                    TODO("What to return when deleted?")
                }
        }
    }

    @GetMapping(Uris.INVITE_LINK_PATH, produces = ["application/vnd.siren+json"])
    fun enterClassroom(
        user: User,
        @PathVariable inviteLink: String,
    ): ResponseEntity<*> {
        if (user !is Student) return Problem.unauthorized
        return when (val classroom = classroomServices.enterClassroom(user.id, inviteLink)) {
            is Either.Left -> problem(classroom.value)
            is Either.Right -> siren(value = ClassroomOutputModel(id = classroom.value.id, name = classroom.value.name, isArchived = classroom.value.isArchived, lastSync = classroom.value.lastSync, assigments = classroom.value.assigments, students = classroom.value.students)) {
                clazz("classroom")
                link(rel = LinkRelation("classroom"), href = Uris.classroomUri(classroom.value.id), needAuthentication = true)
            }
        }
    }

    @PostMapping(Uris.SYNC_CLASSROOM_PATH, produces = ["application/vnd.siren+json"])
    fun syncClassroom(
        user: User,
        @PathVariable classroomId: Int,
        @PathVariable courseId: Int,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.unauthorized
        TODO("Let this be the last thing to do")
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
                link(rel = LinkRelation("classroom"), href = Uris.classroomUri(classroomId), needAuthentication = true)
            }
        }
    }

    private fun problem(error: ClassroomServicesError): ResponseEntity<*> {
        return when (error) {
            ClassroomServicesError.ClasroomNotFound -> Problem.notFound
            ClassroomServicesError.ClassroomArchived -> Problem.invalidOperation
        }
    }
}
