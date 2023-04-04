package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.domain.input.DeliveryInput
import com.isel.leic.ps.ion_classcode.http.model.output.DeliveryOutputModel
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.stereotype.Component

typealias DeliveryResponse = Either<DeliveryServicesError, DeliveryOutputModel>
typealias DeliveryCreatedResponse = Either<DeliveryServicesError, Int>
typealias DeliveryDeletedResponse = Either<DeliveryServicesError, Boolean>

sealed class DeliveryServicesError {
    object NotTeacher : DeliveryServicesError()
    object InvalidInput : DeliveryServicesError()
    object DeliveryNotFound : DeliveryServicesError()
}

@Component
class DeliveryServices(
    val transactionManager: TransactionManager,
) {
    // TODO: updateDelivery
    // TODO: deleteDelivery
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
                    DeliveryOutputModel(
                        delivery,
                        teams.filter { team -> team.id in teamsDelivered },
                        teams.filter { team -> team.id !in teamsDelivered },
                    ),
                )
            }
        }
    }
}
