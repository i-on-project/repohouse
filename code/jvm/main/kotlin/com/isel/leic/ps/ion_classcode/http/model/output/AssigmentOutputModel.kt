package com.isel.leic.ps.ion_classcode.http.model.output

import com.isel.leic.ps.ion_classcode.domain.Assignment
import com.isel.leic.ps.ion_classcode.domain.Delivery
import com.isel.leic.ps.ion_classcode.domain.Team

/**
 * Represents a Assigment Output Model.
 */
data class AssigmentOutputModel(
    val assignment: Assignment,
    val deliveries: List<Delivery>,
    val teams: List<Team>,
)

/**
 * Represents a Assigment Created Output Model.
 */
data class AssigmentCreatedOutputModel(
    val assignment: Assignment,
)

/**
 * Represents a Assigment Model for inner functions.
 */
data class AssignmentModel(
    val assignment: Assigment,
    val deliveries: List<Delivery>,
    val teams: List<Team>,
)
