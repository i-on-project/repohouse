package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.http.model.input.ClassroomUpdateInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomArchivedModel
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import java.io.File

typealias ClassroomResponse = Either<ClassroomServicesError, ClassroomModel>
typealias ClassroomArchivedResponse = Either<ClassroomServicesError, ClassroomArchivedModel>
typealias ClassroomCreateResponse = Either<ClassroomServicesError, Int>
typealias ClassroomEnterResponse = Either<ClassroomServicesError, ClassroomModel>
typealias ClassroomSyncResponse = Either<ClassroomServicesError, Boolean>
typealias ClassroomLocalCopyResponse = Either<ClassroomServicesError, Boolean>

sealed class ClassroomServicesError {
    object ClassroomNotFound : ClassroomServicesError()
    object ClassroomArchived : ClassroomServicesError()
    object AlreadyInClassroom : ClassroomServicesError()
    object InvalidInput : ClassroomServicesError()
}

@Component
class ClassroomServices(
    private val transactionManager: TransactionManager,
    private val deliveryServices: DeliveryServices,
) {

    fun getClassroom(classroomId: Int): ClassroomResponse {
        if (classroomId < 0) return Either.Left(value = ClassroomServicesError.InvalidInput)
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomById(classroomId = classroomId)) {
                null -> Either.Left(value = ClassroomServicesError.ClassroomNotFound)
                else -> {
                    val assignments = it.assignmentRepository.getAssignmentsByClassroom(classroomId = classroomId)
                    val students = it.classroomRepository.getStudentsByClassroom(classroomId = classroomId)
                    Either.Right(value = ClassroomModel(id = classroom.id, name = classroom.name, isArchived = classroom.isArchived, lastSync = classroom.lastSync, assignments = assignments, students = students))
                }
            }
        }
    }

    fun createClassroom(classroomInput: ClassroomInput): ClassroomCreateResponse {
        if (classroomInput.isNotValid()) return Either.Left(value = ClassroomServicesError.InvalidInput)
        return transactionManager.run {
            val otherInviteLinks = it.classroomRepository.getAllInviteLinks()
            val inviteLink = generateRandomInviteLink(otherInviteLinks = otherInviteLinks)
            val classroomId = it.classroomRepository.createClassroom(classroomInput, inviteLink)
            Either.Right(value = classroomId)
        }
    }

    fun archiveOrDeleteClassroom(classroomId: Int): ClassroomArchivedResponse {
        if (classroomId < 0) return Either.Left(value = ClassroomServicesError.InvalidInput)
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomById(classroomId = classroomId)) { // Safety check
                null -> Either.Left(value = ClassroomServicesError.ClassroomNotFound)
                else -> {
                    if (classroom.isArchived) return@run Either.Right(value = ClassroomArchivedModel.ClassroomArchived)
                    val assignments = it.assignmentRepository.getAssignmentsByClassroom(classroomId = classroomId)
                    if (assignments.isEmpty()) {
                        it.classroomRepository.deleteClassroom(classroomId = classroomId)
                        Either.Right(value = ClassroomArchivedModel.ClassroomDeleted)
                    } else {
                        it.classroomRepository.archiveClassroom(classroomId = classroomId)
                        Either.Right(value = ClassroomArchivedModel.ClassroomArchived)
                    }
                }
            }
        }
    }

    fun editClassroom(classroomId: Int, classroomUpdateInput: ClassroomUpdateInputModel): ClassroomResponse {
        if (classroomId < 0 || classroomUpdateInput.isNotValid()) return Either.Left(value = ClassroomServicesError.InvalidInput)
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomById(classroomId = classroomId)) {
                null -> Either.Left(value = ClassroomServicesError.ClassroomNotFound)
                else -> {
                    if (classroom.isArchived) return@run Either.Left(value = ClassroomServicesError.ClassroomArchived)
                    it.classroomRepository.updateClassroomName(classroomId = classroomId, classroomUpdate = classroomUpdateInput)
                    val assignments = it.assignmentRepository.getAssignmentsByClassroom(classroomId = classroomId)
                    val students = it.classroomRepository.getStudentsByClassroom(classroomId = classroomId)
                    Either.Right(value = ClassroomModel(id = classroom.id, name = classroomUpdateInput.name, isArchived = classroom.isArchived, lastSync = classroom.lastSync, assignments = assignments, students = students))
                }
            }
        }
    }

    fun enterClassroomWithInvite(inviteLink: String, studentId: Int): ClassroomEnterResponse {
        if (inviteLink.isBlank() || studentId < 0) return Either.Left(ClassroomServicesError.InvalidInput)
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomByInviteLink(inviteLink = inviteLink)) {
                null -> Either.Left(value = ClassroomServicesError.ClassroomNotFound)
                else -> {
                    if (classroom.isArchived) return@run Either.Left(value = ClassroomServicesError.ClassroomArchived)
                    val prevStudents = it.classroomRepository.getStudentsByClassroom(classroomId = classroom.id)
                    if (prevStudents.any { prevStudent -> prevStudent.id == studentId }) return@run Either.Left(value = ClassroomServicesError.AlreadyInClassroom)
                    it.classroomRepository.addStudentToClassroom(classroomId = classroom.id, studentId = studentId)
                    val assignments = it.assignmentRepository.getAssignmentsByClassroom(classroomId = classroom.id)
                    val students = it.classroomRepository.getStudentsByClassroom(classroomId = classroom.id)
                    Either.Right(value = ClassroomModel(id = classroom.id, name = classroom.name, isArchived = classroom.isArchived, lastSync = classroom.lastSync, assignments = assignments, students = students))
                }
            }
        }
    }

    suspend fun syncClassroom(classroomId: Int, userId: Int, courseId: Int): ClassroomSyncResponse {
        val scopeMain = CoroutineScope(Job())
        val couroutines = mutableListOf<Job>()

        transactionManager.run {
            val assginments = it.assignmentRepository.getAssignmentsByClassroom(classroomId)
            assginments.forEach { assigment ->
                val deliveries = it.deliveryRepository.getDeliveriesByAssignment(assigment.id)
                deliveries.forEach { delivery ->
                    val scope = scopeMain.launch {
                        deliveryServices.syncDelivery(delivery.id, userId, courseId)
                    }
                    couroutines.add(scope)
                }
            }
        }
        couroutines.forEach { scope -> scope.join() }
        return Either.Right(true)
    }

    fun localCopy(classroomId: Int, path: String): ClassroomLocalCopyResponse {
        return transactionManager.run {
            val classroom = it.classroomRepository.getClassroomById(classroomId)
                ?: return@run Either.Left(ClassroomServicesError.ClassroomNotFound)
            it.assignmentRepository.getAssignmentsByClassroom(classroomId).forEach { assigment ->
                it.deliveryRepository.getDeliveriesByAssignment(assigment.id).forEach { delivery ->
                    it.deliveryRepository.getTeamsByDelivery(delivery.id).forEach { team ->
                        it.repoRepository.getReposByTeam(team.id).forEach { repo ->
                            val directory = "$path\\ClassCode\\${classroom.name}\\${team.name}"
                            if (File(directory).exists()) {
                                File(directory).listFiles()?.forEach { file ->
                                    deleteDirectoryRecursion(file)
                                }
                            }
                            File(directory).mkdirs()
                            // TODO: If need just folders, user :folder-name
                            ProcessBuilder("git", "clone", repo.url, directory).start()
                        }
                    }
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

    private fun deleteDirectoryRecursion(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { deleteDirectoryRecursion(it) }
        }
        file.delete()
    }
}
