package com.isel.leic.ps.ion_classcode.http.model.output

import com.isel.leic.ps.ion_classcode.domain.Assignment
import com.isel.leic.ps.ion_classcode.domain.Delivery
import com.isel.leic.ps.ion_classcode.domain.Team

data class AssigmentOutputModel(
    val assignment: Assignment,
    val deliveries: List<Delivery>,
    val teams: List<Team>,
)
data class AssigmentCreatedOutputModel(
    val assignment: Assignment,
)

data class AssignmentModel(
    val assignment: Assignment,
    val deliveries: List<Delivery>,
    val teams: List<Team>,
)
