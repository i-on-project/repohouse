package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.input.DeliveryInput
import com.isel.leic.ps.ion_classcode.domain.input.TagInput
import com.isel.leic.ps.ion_classcode.http.model.output.DeliveryModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

/**
 * Alias for the response of the services
 */
typealias DeliveryResponse = Either<DeliveryServicesError, DeliveryModel>
typealias DeliveryCreatedResponse = Either<DeliveryServicesError, Int>
typealias DeliveryDeletedResponse = Either<DeliveryServicesError, Boolean>
typealias DeliveryUpdateResponse = Either<DeliveryServicesError, Boolean>
typealias DeliverySyncResponse = Either<DeliveryServicesError, Boolean>

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
        if (deliveryInfo.isNotValid() || userId <= 0) {
            return Either.Left(value = DeliveryServicesError.InvalidInput)
        }
        return transactionManager.run {
            val isArchived = checkIfArchived(assignmentId = deliveryInfo.assignmentId)
            if (isArchived is Either.Left) return@run isArchived
            if (it.usersRepository.getTeacher(teacherId = userId) == null) return@run Either.Left(value = DeliveryServicesError.NotTeacher)
            val deliveryId = it.deliveryRepository.createDelivery(
                delivery = DeliveryInput(
                    dueDate = deliveryInfo.dueDate,
                    assignmentId = deliveryInfo.assignmentId,
                    tagControl = deliveryInfo.tagControl,
                ),
            )
            Either.Right(value = deliveryId)
        }
    }

    /**
     * Method to get a delivery
     */
    fun getDeliveryInfo(deliveryId: Int): DeliveryResponse {
        if (deliveryId <= 0) return Either.Left(value = DeliveryServicesError.InvalidInput)
        return transactionManager.run {
            val delivery = it.deliveryRepository.getDeliveryById(deliveryId = deliveryId)
                ?: return@run Either.Left(value = DeliveryServicesError.DeliveryNotFound)
            val teamsDelivered = it.deliveryRepository.getTeamsByDelivery(deliveryId = deliveryId)
            val teamsDeliveredIds = teamsDelivered.map { team -> team.id }
            val teams = it.deliveryRepository.getTeamsByDelivery(deliveryId = deliveryId)
            Either.Right(
                value = DeliveryModel(
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
        if (deliveryId <= 0 || userId <= 0) return Either.Left(value = DeliveryServicesError.InvalidInput)
        return transactionManager.run {
            if (it.usersRepository.getTeacher(teacherId = userId) == null) return@run Either.Left(value = DeliveryServicesError.NotTeacher)
            val delivery = it.deliveryRepository.getDeliveryById(deliveryId = deliveryId) ?: return@run Either.Left(value = DeliveryServicesError.DeliveryNotFound)
            val isArchived = checkIfArchived(assignmentId = delivery.assignmentId)
            if (isArchived is Either.Left) return@run isArchived
            val x = it.deliveryRepository.getTeamsByDelivery(deliveryId = deliveryId).isNotEmpty()
            if (it.deliveryRepository.getTeamsByDelivery(deliveryId = deliveryId).isNotEmpty()) Either.Left(value = DeliveryServicesError.DeliveryWithTeams)
            it.deliveryRepository.deleteDelivery(deliveryId = deliveryId)
            Either.Right(true)
        }
    }

    /**
     * Method to update a delivery
     */
    fun updateDelivery(deliveryId: Int, deliveryInfo: DeliveryInput, userId: Int): DeliveryUpdateResponse {
        if (deliveryId <= 0 || userId <= 0 || deliveryInfo.isNotValid()) {
            return Either.Left(value = DeliveryServicesError.InvalidInput)
        }
        return transactionManager.run {
            if (it.usersRepository.getTeacher(teacherId = userId) == null) return@run Either.Left(value = DeliveryServicesError.NotTeacher)
            val delivery = it.deliveryRepository.getDeliveryById(deliveryId = deliveryId) ?: return@run Either.Left(value = DeliveryServicesError.DeliveryNotFound)
            val isArchived = checkIfArchived(assignmentId = delivery.assignmentId)
            if (isArchived is Either.Left) return@run isArchived
            if (delivery.tagControl != deliveryInfo.tagControl) {
                it.deliveryRepository.updateTagControlFromDelivery(deliveryId = deliveryId, tagControl = deliveryInfo.tagControl)
            }
            if (delivery.dueDate != deliveryInfo.dueDate) {
                it.deliveryRepository.updateDueDateFromDelivery(deliveryId = deliveryId, dueDate = deliveryInfo.dueDate)
            }
            Either.Right(value = true)
        }
    }

    /**
     * Method to sync a delivery with the GitHub truth
     */
    suspend fun syncDelivery(deliveryId: Int, userId: Int, courseId: Int): DeliverySyncResponse {
        if (deliveryId <= 0 || userId <= 0 || courseId <= 0) return Either.Left(value = DeliveryServicesError.InvalidInput)
        val scopeMain = CoroutineScope(Job())
        val couroutines = mutableListOf<Job>()

        transactionManager.run {
            val delivery = it.deliveryRepository.getDeliveryById(deliveryId) ?: return@run Either.Left(DeliveryServicesError.DeliveryNotFound)
            val teacherToken = it.usersRepository.getTeacherGithubToken(userId) ?: return@run Either.Left(DeliveryServicesError.NotTeacher)
            val course = it.courseRepository.getCourse(courseId) ?: return@run Either.Left(DeliveryServicesError.CourseNotFound)
            val courseName = course.name
            val teams = it.deliveryRepository.getTeamsByDelivery(deliveryId)
            teams.forEach { team ->
                val repo = it.repoRepository.getReposByTeam(team.id).first() // TODO: Check if there is only one repo
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
                            // TODO: else create student ????
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

        // TODO: Update last sync date

        return Either.Right(value = true)
    }

    /**
     * Method to check if the classroom is archived
     */
    private fun checkIfArchived(assignmentId: Int): Either<DeliveryServicesError, Boolean> {
        val assignment = transactionManager.run {
            it.assignmentRepository.getAssignmentById(assignmentId)
        } ?: return Either.Left(value = DeliveryServicesError.AssignmentNotFound)

        val classroom = transactionManager.run {
            it.classroomRepository.getClassroomById(assignment.classroomId)
        } ?: return Either.Left(value = DeliveryServicesError.ClassroomNotFound)
        return if (classroom.isArchived) {
            Either.Left(value = DeliveryServicesError.ClassroomArchived)
        } else {
            Either.Right(true)
        }
    }
}
