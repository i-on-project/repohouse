package com.isel.leic.ps.ion_classcode.http.model.output

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.Delivery
import com.isel.leic.ps.ion_classcode.domain.Team

/**
 * Represents a Assigment Output Model.
 */
data class AssigmentOutputModel(
    val assigment: Assigment,
    val deliveries: List<Delivery>,
    val teams: List<Team>,
)

/**
 * Represents a Assigment Created Output Model.
 */
data class AssigmentCreatedOutputModel(
    val assigment: Assigment,
)

/**
 * Represents a Assigment Model for inner functions.
 */
data class AssigmentModel(
    val assigment: Assigment,
    val deliveries: List<Delivery>,
    val teams: List<Team>,
)
