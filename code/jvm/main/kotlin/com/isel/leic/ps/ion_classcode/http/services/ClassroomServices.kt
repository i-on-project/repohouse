package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.http.model.input.ClassroomUpdateInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomArchivedOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomOutputModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias ClassroomResponse = Either<ClassroomServicesError, ClassroomOutputModel>
typealias ClassroomArchivedResponse = Either<ClassroomServicesError, ClassroomArchivedOutputModel>
typealias ClassroomCreateResponse = Either<ClassroomServicesError, Int>
typealias ClassroomEnterResponse = Either<ClassroomServicesError, ClassroomOutputModel>

sealed class ClassroomServicesError {
    object ClasroomNotFound : ClassroomServicesError()
    object ClassroomArchived : ClassroomServicesError()
}

@Component
class ClassroomServices(
    private val transactionManager: TransactionManager,
) {

    fun getClassroom(classroomId: Int): ClassroomResponse {
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomById(classroomId)) {
                null -> Either.Left(ClassroomServicesError.ClasroomNotFound)
                else -> {
                    val assigments = it.assigmentRepository.getAssignmentsByClassroom(classroomId)
                    val students = it.classroomRepository.getStudentsByClassroom(classroomId)
                    Either.Right(ClassroomOutputModel(classroom.id, classroom.name, classroom.isArchived, classroom.lastSync, assigments, students))
                }
            }
        }
    }

    fun createClassroom(classroomInput: ClassroomInput): ClassroomCreateResponse {
        return transactionManager.run {
            val otherInviteLinks = it.classroomRepository.getAllInviteLinks()
            val inviteLink = generateRandomInviteLink(otherInviteLinks)
            val classroomId = it.classroomRepository.createClassroom(classroomInput, inviteLink)
            Either.Right(classroomId)
        }
    }

    fun enterClassroom(studentId: Int, inviteLink: String): ClassroomEnterResponse {
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomByInviteLink(inviteLink)) {
                null -> Either.Left(ClassroomServicesError.ClasroomNotFound)
                else -> {
                    if (classroom.isArchived) Either.Left(ClassroomServicesError.ClassroomArchived)
                    it.classroomRepository.addStudentToClassroom(classroom.id, studentId)
                    val assigments = it.assigmentRepository.getAssignmentsByClassroom(classroom.id)
                    val students = it.classroomRepository.getStudentsByClassroom(classroom.id)
                    Either.Right(ClassroomOutputModel(classroom.id, classroom.name, classroom.isArchived, classroom.lastSync, assigments, students))
                }
            }
        }
    }

    fun archiveOrDeleteClassroom(classroomId: Int): ClassroomArchivedResponse {
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomById(classroomId)) { // Safety check
                null -> Either.Left(ClassroomServicesError.ClasroomNotFound)
                else -> {
                    if (classroom.isArchived) Either.Right(ClassroomArchivedOutputModel.ClassroomArchived)
                    val assigments = it.assigmentRepository.getAssignmentsByClassroom(classroomId)
                    if (assigments.isEmpty()) {
                        it.classroomRepository.deleteClassroom(classroomId)
                        Either.Right(ClassroomArchivedOutputModel.ClassroomDeleted)
                    } else {
                        it.classroomRepository.archiveClassroom(classroomId)
                        Either.Right(ClassroomArchivedOutputModel.ClassroomArchived)
                    }
                }
            }
        }
    }

    fun editClassroom(classroomId: Int, classroomUpdateInput: ClassroomUpdateInputModel): ClassroomResponse {
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomById(classroomId)) {
                null -> Either.Left(ClassroomServicesError.ClasroomNotFound)
                else -> {
                    if (classroom.isArchived) Either.Left(ClassroomServicesError.ClassroomArchived)
                    it.classroomRepository.updateClassroomName(classroomId, classroomUpdateInput)
                    val assigments = it.assigmentRepository.getAssignmentsByClassroom(classroomId)
                    val students = it.classroomRepository.getStudentsByClassroom(classroomId)
                    Either.Right(ClassroomOutputModel(classroom.id, classroom.name, classroom.isArchived, classroom.lastSync, assigments, students))
                }
            }
        }
    }

    private fun generateRandomInviteLink(otherInviteLinks: List<String>): String {
        val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val inviteLink = (10..30)
            .map { chars.random() }
            .joinToString("")
        return if (otherInviteLinks.contains(inviteLink)) {
            generateRandomInviteLink(otherInviteLinks)
        } else {
            inviteLink
        }
    }
}
