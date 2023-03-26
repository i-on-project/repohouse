package com.isel.leic.ps.ion_classcode.domain

data class Assigment(
    val id: Int,
    val classroomId: Int,
    val maxNumberElems: Int,
    val maxNumberGroups: Int,
    val releaseDate: Long,
    val description: String,
    val title: String,
)
