package com.isel.leic.ps.ionClassCode.http.model.output

import com.isel.leic.ps.ionClassCode.domain.Delivery
import com.isel.leic.ps.ionClassCode.domain.Team

/**
 * Represents a Delivery Output Model.
 */
data class DeliveryOutputModel(
    val delivery: Delivery,
    val teamsDelivered: List<Team>,
    val teamsNotDelivered: List<Team>,
)

/**
 * Represents a Delivery Deleted Output Model.
 */
data class DeliveryDeleteOutputModel(
    val id: Int,
    val deleted: Boolean,
)

/**
 * Represents a Delivery Model for inner functions.
 */
data class DeliveryModel(
    val delivery: Delivery,
    val teamsDelivered: List<Team>,
    val teamsNotDelivered: List<Team>,
)
