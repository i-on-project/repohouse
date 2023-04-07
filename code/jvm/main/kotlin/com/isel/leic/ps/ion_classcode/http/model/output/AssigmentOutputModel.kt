package com.isel.leic.ps.ion_classcode.http.model.output

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.Delivery
import com.isel.leic.ps.ion_classcode.domain.Team

data class AssigmentOutputModel(
    val assigment: Assigment,
    val deliveries: List<Delivery>,
    val teams: List<Team>,
)
data class AssigmentCreatedOutputModel(
    val assigment: Assigment,
)

data class AssigmentModel(
    val assigment: Assigment,
    val deliveries: List<Delivery>,
    val teams: List<Team>,
)
