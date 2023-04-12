package com.isel.leic.ps.ion_classcode.http.model.input

import java.sql.Timestamp

data class AssignmentInputModel(
    val classroomId: Int,
    val maxNumberElems: Int,
    val maxNumberGroups: Int,
    val description: String,
    val title: String,
    val dueDate: Timestamp,
) {
    fun isNotValid(): Boolean {
        return classroomId <= 0 ||
            maxNumberElems <= 0 ||
            maxNumberGroups <= 0 ||
            description.isBlank() ||
            title.isBlank()
    }
}
