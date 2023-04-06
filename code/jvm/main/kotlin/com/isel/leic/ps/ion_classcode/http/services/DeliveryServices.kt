package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Tags
import com.isel.leic.ps.ion_classcode.domain.input.DeliveryInput
import com.isel.leic.ps.ion_classcode.domain.input.TagInput
import com.isel.leic.ps.ion_classcode.http.model.output.DeliveryModel
import com.isel.leic.ps.ion_classcode.http.model.output.DeliveryOutputModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

typealias DeliveryResponse = Either<DeliveryServicesError, DeliveryModel>
typealias DeliveryCreatedResponse = Either<DeliveryServicesError, Int>
typealias DeliveryDeletedResponse = Either<DeliveryServicesError, Boolean>
typealias DeliveryUpdateResponse = Either<DeliveryServicesError, Boolean>
typealias DeliverySyncResponse = Either<DeliveryServicesError, Boolean>

sealed class DeliveryServicesError {
    object NotTeacher : DeliveryServicesError()
    object InvalidInput : DeliveryServicesError()
    object DeliveryNotFound : DeliveryServicesError()
    object DeliveryWithTeams : DeliveryServicesError()
    object CourseNotFound : DeliveryServicesError()
}

@Component
class DeliveryServices(
    val transactionManager: TransactionManager,
    val githubServices: GithubServices
) {
    // TODO: syncDelivery
    fun createDelivery(deliveryInfo: DeliveryInput, userId: Int): DeliveryCreatedResponse {
        if (
            deliveryInfo.assigmentId > 0 &&
            deliveryInfo.tagControl.isNotBlank()
        ) {
            return Either.Left(DeliveryServicesError.InvalidInput)
        }
        return transactionManager.run {
            if (it.usersRepository.getTeacher(userId) == null) Either.Left(DeliveryServicesError.NotTeacher)
            val deliveryId = it.deliveryRepository.createDelivery(
                DeliveryInput(
                    deliveryInfo.dueDate,
                    deliveryInfo.assigmentId,
                    deliveryInfo.tagControl,
                ),
            )
            Either.Right(deliveryId)
        }
    }

    fun getDeliveryInfo(deliveryId: Int): DeliveryResponse {
        return transactionManager.run {
            val delivery = it.deliveryRepository.getDeliveryById(deliveryId)
            if (delivery == null) {
                Either.Left(DeliveryServicesError.DeliveryNotFound)
            } else {
                val teamsDelivered = it.deliveryRepository.getTeamsByDelivery(deliveryId)
                val teams = it.teamRepository.getTeamsFromAssignment(delivery.assignmentId)
                Either.Right(
                    DeliveryModel(
                        delivery,
                        teams.filter { team -> team.id in teamsDelivered },
                        teams.filter { team -> team.id !in teamsDelivered },
                    ),
                )
            }
        }
    }

    fun deleteDelivery(deliveryId: Int, userId: Int): DeliveryDeletedResponse {
        return transactionManager.run {
            if (it.usersRepository.getTeacher(userId) == null) Either.Left(DeliveryServicesError.NotTeacher)
            val delivery = it.deliveryRepository.getDeliveryById(deliveryId)
            if (delivery == null) {
                Either.Left(DeliveryServicesError.DeliveryNotFound)
            } else {
                if (it.deliveryRepository.getTeamsByDelivery(deliveryId).isNotEmpty()) Either.Left(DeliveryServicesError.DeliveryWithTeams)
                it.deliveryRepository.deleteDelivery(deliveryId)
                Either.Right(true)
            }
        }
    }

    fun updateDelivery(deliveryId: Int, deliveryInfo: DeliveryInput, userId: Int): DeliveryUpdateResponse {
        if (
            deliveryInfo.assigmentId > 0 &&
            deliveryInfo.tagControl.isNotBlank()
        ) {
            return Either.Left(DeliveryServicesError.InvalidInput)
        }
        return transactionManager.run {
            if (it.usersRepository.getTeacher(userId) == null) Either.Left(DeliveryServicesError.NotTeacher)
            val delivery = it.deliveryRepository.getDeliveryById(deliveryId)
            if (delivery == null) {
                Either.Left(DeliveryServicesError.DeliveryNotFound)
            } else {
                if (delivery.tagControl != deliveryInfo.tagControl) {
                    it.deliveryRepository.updateTagControlFromDelivery(deliveryId, deliveryInfo.tagControl)
                }
                if (delivery.dueDate != deliveryInfo.dueDate) {
                    it.deliveryRepository.updateDueDateFromDelivery(deliveryId, deliveryInfo.dueDate)
                }
                Either.Right(true)
            }
        }
    }

    suspend fun syncDelivery(deliveryId: Int, userId: Int, courseId:Int): DeliverySyncResponse {
        val scopeMain = CoroutineScope(Job())
        val couroutines = mutableListOf<Job>()

        transactionManager.run {
            val delivery = it.deliveryRepository.getDeliveryById(deliveryId) ?: return@run Either.Left(DeliveryServicesError.DeliveryNotFound)
            val teacher = it.usersRepository.getTeacher(userId) ?: return@run Either.Left(DeliveryServicesError.NotTeacher)
            val teacherToken = teacher.token
            val course = it.courseRepository.getCourse(courseId) ?: return@run Either.Left(DeliveryServicesError.CourseNotFound)
            val courseName = course.name
            it.deliveryRepository.getTeamsByDelivery(deliveryId).forEach { teamId ->
                val repo = it.repoRepository.getReposByTeam(teamId).first() //TODO: Check if there is only one repo
                val studentsList = it.teamRepository.getStudentsFromTeam(teamId)
                val tagsList = it.tagRepository.getTagsByDelivery(deliveryId)
                val scope = scopeMain.launch {
                    val githubTruth = githubServices.getRepository(repo.name, teacherToken, courseName)

                    val studentsToAdd:List<Student> = studentsList.filter { student ->
                        githubTruth.collaborators.filter { collaborator -> !collaborator.permissions.admin }.map { collaborator -> collaborator.id }.contains(student.githubId.toInt())
                    }
                    val studentsToRemove:List<Student>  = studentsList.filter { student ->
                        !githubTruth.collaborators.filter { collaborator -> !collaborator.permissions.admin }.map { collaborator -> collaborator.id }.contains(student.githubId.toInt())
                    }

                    val tagsToAdd:List<Tags> = tagsList.filter { tag ->
                        !githubTruth.tags.map { githubTag -> githubTag.name }.contains(tag.name)
                    }

                    studentsToAdd.forEach { student ->
                        it.teamRepository.enterTeam(teamId,student.id)
                    }

                    studentsToRemove.forEach { student ->
                        it.teamRepository.leaveTeam(teamId,student.id)
                    }

                    tagsToAdd.forEach { tag ->
                        val isDelivered = tag.tagDate.before(delivery.dueDate) && tag.name.startsWith(delivery.tagControl)
                        it.tagRepository.createTag(TagInput(tag.name,isDelivered,tag.tagDate,deliveryId, repo.id))
                    }
                }
                couroutines.add(scope)
                scope.invokeOnCompletion {
                    couroutines.remove(scope)
                }
            }
        }

        couroutines.forEach { it.join() }

        //TODO: Update last sync date

        return Either.Right(true)
    }
}
