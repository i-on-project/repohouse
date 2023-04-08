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
    object AssignmentNotFound : DeliveryServicesError()
    object ClassroomNotFound : DeliveryServicesError()
    object ClassroomArchived : DeliveryServicesError()
}

@Component
class DeliveryServices(
    val transactionManager: TransactionManager,
    val githubServices: GithubServices
) {
    fun createDelivery(deliveryInfo: DeliveryInput, userId: Int): DeliveryCreatedResponse {
        if (
            deliveryInfo.assigmentId > 0 &&
            deliveryInfo.tagControl.isNotBlank()
        ) {
            return Either.Left(DeliveryServicesError.InvalidInput)
        }
        return transactionManager.run {
            val isArchived = checkIfArchived(deliveryInfo.assigmentId)
            if (isArchived is Either.Left) return@run isArchived
            if (isArchived is Either.Right && isArchived.value) return@run Either.Left(DeliveryServicesError.ClassroomArchived)
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
                val teamsDelivered = it.deliveryRepository.getTeamsByDelivery(deliveryId = deliveryId)
                val teamsDeliveredIds = teamsDelivered.map { team -> team.id }
                val teams = it.deliveryRepository.getTeamsByDelivery(deliveryId = deliveryId)
                Either.Right(
                    DeliveryModel(
                        delivery = delivery,
                        teamsDelivered = teamsDelivered,
                        teams.filter { team -> team.id !in teamsDeliveredIds },
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
                val isArchived = checkIfArchived(delivery.assignmentId)
                if (isArchived is Either.Left) return@run isArchived
                if (isArchived is Either.Right && isArchived.value) return@run Either.Left(DeliveryServicesError.ClassroomArchived)
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
                val isArchived = checkIfArchived(delivery.assignmentId)
                if (isArchived is Either.Left) return@run isArchived
                if (isArchived is Either.Right && isArchived.value) return@run Either.Left(DeliveryServicesError.ClassroomArchived)
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
            val teacherToken = it.usersRepository.getTeacherGithubToken(userId) ?: return@run Either.Left(DeliveryServicesError.NotTeacher)
            val course = it.courseRepository.getCourse(courseId) ?: return@run Either.Left(DeliveryServicesError.CourseNotFound)
            val courseName = course.name
            val teams = it.deliveryRepository.getTeamsByDelivery(deliveryId)
            teams.forEach { team->
                val repo = it.repoRepository.getReposByTeam(team.id).first() //TODO: Check if there is only one repo
                val studentsList = it.teamRepository.getStudentsFromTeam(team.id)
                val tagsList = it.tagRepository.getTagsByDelivery(deliveryId)
                var studentsToAdd =listOf<Collaborator>()
                var studentsToRemove = listOf<Student>()
                var tagsToAdd = listOf<Tag>()


                val scope = scopeMain.launch {
                    val githubTruth = githubServices.getRepository(repo.name, teacherToken, courseName)

                    studentsToAdd = githubTruth.collaborators.filter { collaborator ->
                        !studentsList.map { student -> student.githubId.toInt() }.contains(collaborator.id)
                                && !collaborator.permissions.admin
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
                            //TODO: else create student ????
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

        //TODO: Update last sync date

        return Either.Right(true)
    }

    private fun checkIfArchived(assignmentId: Int): Either<DeliveryServicesError, Boolean> {
        val assignment = transactionManager.run {
            it.assigmentRepository.getAssignmentById(assignmentId)
        } ?: return Either.Left(DeliveryServicesError.AssignmentNotFound)

        val classroom = transactionManager.run {
            it.classroomRepository.getClassroomById(assignment.classroomId)
        } ?: return Either.Left(DeliveryServicesError.ClassroomNotFound)

        return Either.Right(classroom.isArchived)
    }
}
