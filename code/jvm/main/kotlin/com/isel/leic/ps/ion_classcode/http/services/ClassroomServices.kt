package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.http.model.input.ClassroomUpdateInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomArchivedModel
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias ClassroomResponse = Either<ClassroomServicesError, ClassroomModel>
typealias ClassroomArchivedResponse = Either<ClassroomServicesError, ClassroomArchivedModel>
typealias ClassroomCreateResponse = Either<ClassroomServicesError, Int>
typealias ClassroomEnterResponse = Either<ClassroomServicesError, ClassroomModel>
typealias ClassroomSyncResponse = Either<ClassroomServicesError, Boolean>
typealias ClassroomLocalCopyResponse = Either<ClassroomServicesError, Boolean>

/**
 * Error codes for the services
 */
sealed class ClassroomServicesError {
    object ClasroomNotFound : ClassroomServicesError()
    object ClassroomArchived : ClassroomServicesError()
    object AlreadyInClassroom : ClassroomServicesError()
}

/**
 * Services for the classroom
 */
@Component
class ClassroomServices(
    private val transactionManager: TransactionManager,
    private val deliveryServices: DeliveryServices
) {

    /**
     * Method that gets a classroom
     */
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

    /**
     * Method that creates a classroom
     */
    fun createClassroom(classroomInput: ClassroomInput): ClassroomCreateResponse {
        return transactionManager.run {
            val otherInviteLinks = it.classroomRepository.getAllInviteLinks()
            val inviteLink = generateRandomInviteLink(otherInviteLinks)
            val classroomId = it.classroomRepository.createClassroom(classroomInput, inviteLink)
            Either.Right(classroomId)
        }
    }

    /**
     * Method that archives or deletes a classroom
     * If the classroom has no assignments, it is deleted
     */
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

    /**
     * Method that edits a classroom
     */
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

    /**
     * Method to enter a classroom with an invitation link
     */
    fun enterClassroomWithInvite(inviteLink: String,studentId: Int): ClassroomEnterResponse {
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomByInviteLink(inviteLink)) {
                null -> Either.Left(ClassroomServicesError.ClasroomNotFound)
                else -> {
                    if (classroom.isArchived) Either.Left(ClassroomServicesError.ClassroomArchived)
                    val prevStudents = it.classroomRepository.getStudentsByClassroom(classroom.id)
                    if(prevStudents.any { prevStudent -> prevStudent.id == studentId }) Either.Left(ClassroomServicesError.AlreadyInClassroom)
                    it.classroomRepository.addStudentToClassroom(classroom.id,studentId)
                    val assigments = it.assigmentRepository.getAssignmentsByClassroom(classroom.id)
                    val students = it.classroomRepository.getStudentsByClassroom(classroom.id)
                    Either.Right(ClassroomModel(classroom.id, classroom.name, classroom.isArchived, classroom.lastSync, assigments, students))
                }
            }
        }
    }

    /**
     * Method to sync the classroom with the GitHub truth
     */
    suspend fun syncClassroom(classroomId: Int, userId: Int,courseId:Int): ClassroomSyncResponse {
        val scopeMain = CoroutineScope(Job())
        val couroutines = mutableListOf<Job>()

        transactionManager.run {
            val assginments = it.assigmentRepository.getAssignmentsByClassroom(classroomId)
            assginments.forEach {assigment ->
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

    /**
     * Method to get the local copy of the classroom to path in the personal computer
     */
    fun localCopy(classroomId: Int,path:String): ClassroomLocalCopyResponse{
        return transactionManager.run {
            val classroom = it.classroomRepository.getClassroomById(classroomId)
                ?: return@run Either.Left(ClassroomServicesError.ClasroomNotFound)
            it.assigmentRepository.getAssignmentsByClassroom(classroomId).forEach { assigment ->
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

    /**
     * Method to generate a random invite link
     */
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

    /**
     * Method to delete a directory recursively
     */
    private fun deleteDirectoryRecursion(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { deleteDirectoryRecursion(it) }
        }
        file.delete()
    }
}
