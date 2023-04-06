package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.http.model.input.ClassroomUpdateInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomArchivedModel
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias ClassroomResponse = Either<ClassroomServicesError, ClassroomModel>
typealias ClassroomArchivedResponse = Either<ClassroomServicesError, ClassroomArchivedModel>
typealias ClassroomCreateResponse = Either<ClassroomServicesError, Int>
typealias ClassroomEnterResponse = Either<ClassroomServicesError, ClassroomModel>
typealias ClassroomSyncResponse = Either<ClassroomServicesError, Boolean>

sealed class ClassroomServicesError {
    object ClasroomNotFound : ClassroomServicesError()
    object ClassroomArchived : ClassroomServicesError()
    object AlreadyInClassroom : ClassroomServicesError()
}

@Component
class ClassroomServices(
    private val transactionManager: TransactionManager,
    private val deliveryServices: DeliveryServices
) {

    fun getClassroom(classroomId: Int): ClassroomResponse {
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomById(classroomId)) {
                null -> Either.Left(ClassroomServicesError.ClasroomNotFound)
                else -> {
                    val assigments = it.assigmentRepository.getAssignmentsByClassroom(classroomId)
                    val students = it.classroomRepository.getStudentsByClassroom(classroomId)
                    Either.Right(ClassroomModel(classroom.id, classroom.name, classroom.isArchived, classroom.lastSync, assigments, students))
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
                    Either.Right(ClassroomModel(classroom.id, classroom.name, classroom.isArchived, classroom.lastSync, assigments, students))
                }
            }
        }
    }

    fun archiveOrDeleteClassroom(classroomId: Int): ClassroomArchivedResponse {
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomById(classroomId)) { // Safety check
                null -> Either.Left(ClassroomServicesError.ClasroomNotFound)
                else -> {
                    if (classroom.isArchived) Either.Right(ClassroomArchivedModel.ClassroomArchived)
                    val assigments = it.assigmentRepository.getAssignmentsByClassroom(classroomId)
                    if (assigments.isEmpty()) {
                        it.classroomRepository.deleteClassroom(classroomId)
                        Either.Right(ClassroomArchivedModel.ClassroomDeleted)
                    } else {
                        it.classroomRepository.archiveClassroom(classroomId)
                        Either.Right(ClassroomArchivedModel.ClassroomArchived)
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
                    Either.Right(ClassroomModel(classroom.id, classroom.name, classroom.isArchived, classroom.lastSync, assigments, students))
                }
            }
        }
    }

    fun enterClassroomWithInvite(inviteLink: String,studentId: Int): ClassroomEnterResponse {
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomByInviteLink(inviteLink)) {
                null -> Either.Left(ClassroomServicesError.ClasroomNotFound)
                else -> {
                    if (classroom.isArchived) Either.Left(ClassroomServicesError.ClassroomArchived)
                    val prevStudents = it.classroomRepository.getStudentsByClassroom(classroom.id)
                    if(prevStudents.any { it.id == studentId }) Either.Left(ClassroomServicesError.AlreadyInClassroom)
                    it.classroomRepository.addStudentToClassroom(classroom.id,studentId)
                    val assigments = it.assigmentRepository.getAssignmentsByClassroom(classroom.id)
                    val students = it.classroomRepository.getStudentsByClassroom(classroom.id)
                    Either.Right(ClassroomModel(classroom.id, classroom.name, classroom.isArchived, classroom.lastSync, assigments, students))
                }
            }
        }
    }

    suspend fun syncClassroom(classroomId: Int, userId: Int,courseId:Int): ClassroomSyncResponse {
        return transactionManager.run {
            val assginments = it.assigmentRepository.getAssignmentsByClassroom(classroomId)
            assginments.forEach {assigment ->
                val deliveries = it.deliveryRepository.getDeliveriesByAssigment(assigment.id)
                deliveries.forEach { delivery ->
                    //TODO: Check first from delivery
                    // deliveryServices.syncDelivery(delivery.id, userId, courseId)
                }
            }
            Either.Right(true)
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
