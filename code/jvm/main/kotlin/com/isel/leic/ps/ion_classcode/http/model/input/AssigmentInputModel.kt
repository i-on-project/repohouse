package com.isel.leic.ps.ion_classcode.http.model.input

data class AssigmentInputModel(
    val classroomId: Int,
    val maxNumberElems: Int,
    val maxNumberGroups: Int,
    val description: String,
    val title: String
)
