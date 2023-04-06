package com.isel.leic.ps.ion_classcode.http.model.output

import com.isel.leic.ps.ion_classcode.domain.Delivery
import com.isel.leic.ps.ion_classcode.domain.Team

data class DeliveryOutputModel(
    val delivery: Delivery,
    val teamsDelivered: List<Team>,
    val teamsNotDelivered: List<Team>,
)

data class DeliveryDeleteOutputModel(
    val id: Int,
    val deleted: Boolean,
)

data class DeliveryModel(
    val delivery: Delivery,
    val teamsDelivered: List<Team>,
    val teamsNotDelivered: List<Team>,
)
