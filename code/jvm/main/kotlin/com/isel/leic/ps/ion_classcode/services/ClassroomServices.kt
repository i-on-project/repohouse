package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.http.model.input.ClassroomUpdateInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomArchivedResult
import com.isel.leic.ps.ion_classcode.http.model.output.ClassroomModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.io.File

/**
 * Alias for the response of the services
 */
typealias ClassroomResponse = Result<ClassroomServicesError, ClassroomModel>
typealias ClassroomArchivedResponse = Result<ClassroomServicesError, ClassroomArchivedResult>
typealias ClassroomCreateResponse = Result<ClassroomServicesError, ClassroomModel>
typealias ClassroomEnterResponse = Result<ClassroomServicesError, ClassroomModel>
typealias ClassroomSyncResponse = Result<ClassroomServicesError, Boolean>
typealias ClassroomLocalCopyResponse = Result<ClassroomServicesError, Boolean>

/**
 * Error codes for the services
 */
sealed class ClassroomServicesError {
    object ClassroomNotFound : ClassroomServicesError()
    object ClassroomArchived : ClassroomServicesError()
    object AlreadyInClassroom : ClassroomServicesError()
    object InviteLinkNotFound : ClassroomServicesError()
    object InvalidInput : ClassroomServicesError()
    object InternalError: ClassroomServicesError()
}

/**
 * Services for the classroom
 */
@Component
class ClassroomServices(
    private val transactionManager: TransactionManager,
    private val deliveryServices: DeliveryServices,
) {

    /**
     * Method that gets a classroom
     */
    fun getClassroom(classroomId: Int): ClassroomResponse {
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomById(classroomId)) {
                null -> Result.Problem(ClassroomServicesError.ClassroomNotFound)
                else -> {
                    val assignments = it.assignmentRepository.getAssignmentsByClassroom(classroomId)
                    val students = it.classroomRepository.getStudentsByClassroom(classroomId)
                    Result.Success(ClassroomModel(
                        id = classroom.id,
                        name = classroom.name,
                        isArchived = classroom.isArchived,
                        lastSync = classroom.lastSync,
                        assignments = assignments,
                        students = students
                    ))
                }
            }
        }
    }

    /**
     * Method that creates a classroom
     */
    fun createClassroom(classroomInput: ClassroomInput): ClassroomCreateResponse {
        if (classroomInput.isNotValid()) return Result.Problem(ClassroomServicesError.InvalidInput)
        return transactionManager.run {
            val otherInviteLinks = it.classroomRepository.getAllInviteLinks()
            val inviteLink = generateRandomInviteLink(otherInviteLinks)
            val classroom = it.classroomRepository.createClassroom(classroomInput, inviteLink)
            if (classroom == null) Result.Problem(ClassroomServicesError.InternalError)
            else Result.Success(ClassroomModel(
                id = classroom.id,
                name = classroom.name,
                isArchived = classroom.isArchived,
                lastSync = classroom.lastSync,
                assignments = listOf(),
                students = listOf()
            ))
        }
    }

    /**
     * Method that archives or deletes a classroom
     * If the classroom has no assignments, it is deleted
     */
    fun archiveOrDeleteClassroom(classroomId: Int): ClassroomArchivedResponse {
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomById(classroomId)) {
                null -> Result.Problem(ClassroomServicesError.ClassroomNotFound)
                else -> {
                    if (classroom.isArchived) Result.Success(ClassroomArchivedResult.ClassroomArchived)
                    val assignments = it.assignmentRepository.getAssignmentsByClassroom(classroomId)
                    if (assignments.isEmpty()) {
                        it.classroomRepository.deleteClassroom(classroomId)
                        Result.Success(ClassroomArchivedResult.ClassroomDeleted)
                    } else {
                        it.classroomRepository.archiveClassroom(classroomId)
                        Result.Success(ClassroomArchivedResult.ClassroomArchived)
                    }
                }
            }
        }
    }

    /**
     * Method that edits a classroom
     */
    fun editClassroom(classroomId: Int, classroomUpdateInput: ClassroomUpdateInputModel): ClassroomResponse {
        if (classroomUpdateInput.isNotValid()) return Result.Problem(ClassroomServicesError.InvalidInput)
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomById(classroomId)) {
                null -> Result.Problem(ClassroomServicesError.ClassroomNotFound)
                else -> {
                    if (classroom.isArchived) Result.Problem(ClassroomServicesError.ClassroomArchived)
                    it.classroomRepository.updateClassroomName(classroomId, classroomUpdateInput)
                    val assignments = it.assignmentRepository.getAssignmentsByClassroom(classroomId)
                    val students = it.classroomRepository.getStudentsByClassroom(classroomId)
                    Result.Success(ClassroomModel(
                        id = classroom.id,
                        name = classroomUpdateInput.name,
                        isArchived = classroom.isArchived,
                        lastSync = classroom.lastSync,
                        assignments = assignments,
                        students = students
                    ))
                }
            }
        }
    }

    /**
     * Method to enter a classroom with an invitation link
     */
    fun enterClassroomWithInvite(inviteLink: String, studentId: Int): ClassroomEnterResponse {
        if (inviteLink.isBlank()) return Result.Problem(ClassroomServicesError.InviteLinkNotFound)
        return transactionManager.run {
            when (val classroom = it.classroomRepository.getClassroomByInviteLink(inviteLink)) {
                null -> Result.Problem(ClassroomServicesError.InviteLinkNotFound)
                else -> {
                    if (classroom.isArchived) Result.Problem(ClassroomServicesError.ClassroomArchived)
                    val prevStudents = it.classroomRepository.getStudentsByClassroom(classroom.id)
                    if (prevStudents.any { prevStudent -> prevStudent.id == studentId }) Result.Problem(
                        ClassroomServicesError.AlreadyInClassroom
                    )
                    it.classroomRepository.addStudentToClassroom(classroom.id, studentId)
                    val assignments = it.assignmentRepository.getAssignmentsByClassroom(classroom.id)
                    val students = it.classroomRepository.getStudentsByClassroom(classroom.id)
                    Result.Success(ClassroomModel(
                        id = classroom.id,
                        name = classroom.name,
                        isArchived = classroom.isArchived,
                        lastSync = classroom.lastSync,
                        assignments = assignments,
                        students = students
                    ))
                }
            }
        }
    }

    /**
     * Method to sync the classroom with the GitHub truth
     */
    suspend fun syncClassroom(classroomId: Int, userId: Int, courseId: Int): ClassroomSyncResponse {
        val scopeMain = CoroutineScope(Job())
        val coroutines = mutableListOf<Job>()

        transactionManager.run {
            val assignments = it.assignmentRepository.getAssignmentsByClassroom(classroomId)
            assignments.forEach { assigment ->
                val deliveries = it.deliveryRepository.getDeliveriesByAssignment(assigment.id)
                deliveries.forEach { delivery ->
                    val scope = scopeMain.launch {
                        deliveryServices.syncDelivery(delivery.id, userId, courseId)
                    }
                    coroutines.add(scope)
                }
            }
        }
        coroutines.forEach { scope -> scope.join() }
        return Result.Success(true)
    }

    /**
     * Method to get the local copy of the classroom to path in the personal computer
     */
    fun localCopy(classroomId: Int, path: String): ClassroomLocalCopyResponse {
        return transactionManager.run {
            val classroom = it.classroomRepository.getClassroomById(classroomId)
                ?: return@run Result.Problem(ClassroomServicesError.ClassroomNotFound)
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
            Result.Success(true)
        }
    }

    /**
     * Function to handle the errors
     */
    fun problem(error: ClassroomServicesError): ResponseEntity<*> {
        return when (error) {
            ClassroomServicesError.ClassroomNotFound -> Problem.notFound
            ClassroomServicesError.ClassroomArchived -> Problem.invalidOperation
            ClassroomServicesError.AlreadyInClassroom -> Problem.invalidOperation
            ClassroomServicesError.InvalidInput -> Problem.invalidInput
            ClassroomServicesError.InviteLinkNotFound -> Problem.notFound
            ClassroomServicesError.InternalError -> Problem.internalError
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
