package com.isel.leic.ps.ionClassCode.services

import com.isel.leic.ps.ionClassCode.domain.Delivery
import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.input.DeliveryInput
import com.isel.leic.ps.ionClassCode.domain.input.TagInput
import com.isel.leic.ps.ionClassCode.http.model.github.Collaborator
import com.isel.leic.ps.ionClassCode.http.model.github.Tag
import com.isel.leic.ps.ionClassCode.http.model.output.DeliveryModel
import com.isel.leic.ps.ionClassCode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ionClassCode.http.model.problem.Problem
import com.isel.leic.ps.ionClassCode.repository.transaction.Transaction
import com.isel.leic.ps.ionClassCode.repository.transaction.TransactionManager
import com.isel.leic.ps.ionClassCode.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias DeliveryResponse = Result<DeliveryServicesError, DeliveryModel>
typealias DeliveryCreatedResponse = Result<DeliveryServicesError, Delivery>
typealias DeliveryDeletedResponse = Result<DeliveryServicesError, Boolean>
typealias DeliveryUpdateResponse = Result<DeliveryServicesError, Boolean>
typealias DeliverySyncResponse = Result<DeliveryServicesError, Boolean>

/**
 * Error codes for the services
 */
sealed class DeliveryServicesError {
    object NotTeacher : DeliveryServicesError()
    object InvalidInput : DeliveryServicesError()
    object DeliveryNotFound : DeliveryServicesError()
    object DeliveryWithTeams : DeliveryServicesError()
    object CourseNotFound : DeliveryServicesError()
    object AssignmentNotFound : DeliveryServicesError()
    object ClassroomNotFound : DeliveryServicesError()
    object ClassroomArchived : DeliveryServicesError()
    object InternalError : DeliveryServicesError()
}

/**
 * Services for the delivery
 */
@Component
class DeliveryServices(
    val transactionManager: TransactionManager,
    val githubServices: GithubServices,
) {

    /**
     * Method that creates a delivery
     */
    fun createDelivery(deliveryInfo: DeliveryInput, userId: Int): DeliveryCreatedResponse {
        if (deliveryInfo.isNotValid()) return Result.Problem(DeliveryServicesError.InvalidInput)
        return transactionManager.run {
            if (it.usersRepository.getTeacher(userId) == null) return@run Result.Problem(DeliveryServicesError.InternalError)
            if (it.assignmentRepository.getAssignmentById(deliveryInfo.assignmentId) == null) return@run Result.Problem(DeliveryServicesError.AssignmentNotFound)
            val isArchived = checkIfArchived(it, deliveryInfo.assignmentId)
            if (isArchived is Result.Problem) return@run isArchived
            val deliveryId = it.deliveryRepository.createDelivery(
                DeliveryInput(
                    dueDate = deliveryInfo.dueDate,
                    assignmentId = deliveryInfo.assignmentId,
                    tagControl = deliveryInfo.tagControl,
                ),
            )
            Result.Success(deliveryId)
        }
    }

    /**
     * Method to get a delivery
     */
    fun getDeliveryInfo(deliveryId: Int): DeliveryResponse {
        return transactionManager.run {
            val delivery = it.deliveryRepository.getDeliveryById(deliveryId) ?: return@run Result.Problem(DeliveryServicesError.DeliveryNotFound)
            val teamsDelivered = it.deliveryRepository.getTeamsByDelivery(deliveryId)
            val teamsDeliveredIds = teamsDelivered.map { team -> team.id }
            val teams = it.deliveryRepository.getTeamsByDelivery(deliveryId)
            Result.Success(
                DeliveryModel(
                    delivery = delivery,
                    teamsDelivered = teamsDelivered,
                    teamsNotDelivered = teams.filter { team -> team.id !in teamsDeliveredIds },
                ),
            )
        }
    }

    /**
     * Method to delete a delivery
     * Only if the delivery has no teams
     */
    fun deleteDelivery(deliveryId: Int, userId: Int): DeliveryDeletedResponse {
        return transactionManager.run {
            if (it.usersRepository.getTeacher(userId) == null) return@run Result.Problem(DeliveryServicesError.InternalError)
            val delivery = it.deliveryRepository.getDeliveryById(deliveryId) ?: return@run Result.Problem(DeliveryServicesError.DeliveryNotFound)
            val isArchived = checkIfArchived(it, delivery.assignmentId)
            if (isArchived is Result.Problem) return@run isArchived
            if (it.deliveryRepository.getTeamsByDelivery(deliveryId).isNotEmpty()) Result.Problem(DeliveryServicesError.DeliveryWithTeams)
            it.deliveryRepository.deleteDelivery(deliveryId)
            Result.Success(true)
        }
    }

    /**
     * Method to update a delivery
     */
    fun updateDelivery(deliveryId: Int, deliveryInfo: DeliveryInput, userId: Int): DeliveryUpdateResponse {
        if (deliveryInfo.isNotValid()) return Result.Problem(DeliveryServicesError.InvalidInput)
        return transactionManager.run {
            it.usersRepository.getTeacher(userId) ?: return@run Result.Problem(DeliveryServicesError.InternalError)
            val delivery = it.deliveryRepository.getDeliveryById(deliveryId) ?: return@run Result.Problem(DeliveryServicesError.DeliveryNotFound)
            val isArchived = checkIfArchived(it, delivery.assignmentId)
            if (isArchived is Result.Problem) return@run isArchived
            if (delivery.tagControl != deliveryInfo.tagControl) {
                it.deliveryRepository.updateTagControlFromDelivery(deliveryId = deliveryId, tagControl = deliveryInfo.tagControl)
            }
            if (delivery.dueDate != deliveryInfo.dueDate) {
                it.deliveryRepository.updateDueDateFromDelivery(deliveryId = deliveryId, dueDate = deliveryInfo.dueDate)
            }
            Result.Success(value = true)
        }
    }

    /**
     * Method to sync a delivery with the GitHub truth
     */
    suspend fun syncDelivery(deliveryId: Int, userId: Int, courseId: Int): DeliverySyncResponse {
        if (deliveryId <= 0 || userId <= 0 || courseId <= 0) return Result.Problem(value = DeliveryServicesError.InvalidInput)
        val scopeMain = CoroutineScope(Job())
        val couroutines = mutableListOf<Job>()

        transactionManager.run {
            val delivery = it.deliveryRepository.getDeliveryById(deliveryId) ?: return@run Result.Problem(
                DeliveryServicesError.DeliveryNotFound,
            )
            val teacherToken = it.usersRepository.getTeacherGithubToken(userId) ?: return@run Result.Problem(
                DeliveryServicesError.NotTeacher,
            )
            val course = it.courseRepository.getCourse(courseId) ?: return@run Result.Problem(DeliveryServicesError.CourseNotFound)
            val courseName = course.name
            val teams = it.deliveryRepository.getTeamsByDelivery(deliveryId)
            teams.forEach { team ->
                val repo = it.repoRepository.getReposByTeam(team.id).first() // TODO: Check if there is only one repo - just after mobile implementation
                val studentsList = it.teamRepository.getStudentsFromTeam(team.id)
                val tagsList = it.tagRepository.getTagsByDelivery(deliveryId)
                var studentsToAdd = listOf<Collaborator>()
                var studentsToRemove = listOf<Student>()
                var tagsToAdd = listOf<Tag>()

                val scope = scopeMain.launch {
                    val githubTruth = githubServices.getRepository(repo.name, teacherToken, courseName)

                    studentsToAdd = githubTruth.collaborators.filter { collaborator ->
                        !studentsList.map { student -> student.githubId.toInt() }.contains(collaborator.id) &&
                            !collaborator.permissions.admin
                    }

                    studentsToRemove = studentsList.filter { student ->
                        !githubTruth.collaborators.filter { collaborator -> !collaborator.permissions.admin }
                            .map { collaborator -> collaborator.id }.contains(student.githubId.toInt())
                    }

                    tagsToAdd = githubTruth.tags.filter { githubTag ->
                        !tagsList.map { tag -> tag.name }.contains(githubTag.name)
                    }
                }

                couroutines.add(scope)

                scope.invokeOnCompletion {
                    transactionManager.run {
                        studentsToAdd.forEach { studentGithub ->
                            val student = it.usersRepository.getUserByGithubId(studentGithub.id.toLong())
                            if (student != null && student is Student) {
                                it.teamRepository.enterTeam(team.id, student.id)
                            }
                        }

                        studentsToRemove.forEach { student ->
                            it.teamRepository.leaveTeam(team.id, student.id)
                        }

                        tagsToAdd.forEach { tag ->
                            val isDelivered = tag.date.before(delivery.dueDate) && tag.name.startsWith(delivery.tagControl)
                            it.tagRepository.createTag(TagInput(tag.name, isDelivered, tag.date, deliveryId, repo.id))
                        }
                    }
                }
            }
        }
        couroutines.forEach { it.join() }

        transactionManager.run {
            it.deliveryRepository.updateSyncTimeFromDelivery(deliveryId = deliveryId)
        }

        return Result.Success(value = true)
    }

    /**
     * Method to check if the classroom is archived
     */
    private fun checkIfArchived(transaction: Transaction, assignmentId: Int): Result<DeliveryServicesError, Boolean> {
        val assignment = transaction.assignmentRepository.getAssignmentById(assignmentId) ?: return Result.Problem(DeliveryServicesError.AssignmentNotFound)
        val classroom = transaction.classroomRepository.getClassroomById(assignment.classroomId) ?: return Result.Problem(DeliveryServicesError.ClassroomNotFound)
        return if (classroom.isArchived) {
            Result.Problem(DeliveryServicesError.ClassroomArchived)
        } else {
            Result.Success(true)
        }
    }

    /**
     * Function to handle the errors
     */
    fun problem(error: DeliveryServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            DeliveryServicesError.DeliveryNotFound -> Problem.notFound
            DeliveryServicesError.InvalidInput -> Problem.invalidInput
            DeliveryServicesError.NotTeacher -> Problem.notTeacher
            DeliveryServicesError.DeliveryWithTeams -> Problem.invalidOperation
            DeliveryServicesError.CourseNotFound -> Problem.notFound
            DeliveryServicesError.AssignmentNotFound -> Problem.notFound
            DeliveryServicesError.ClassroomArchived -> Problem.invalidOperation
            DeliveryServicesError.ClassroomNotFound -> Problem.notFound
            DeliveryServicesError.InternalError -> Problem.internalError
        }
    }
}
