package com.isel.leic.ps.ionClassCode.http.model.input

import java.sql.Timestamp

/**
 * Represents a Assignment Input Model.
 */
data class AssignmentInputModel(
    val classroomId: Int,
    val minNumberElems: Int,
    val maxNumberElems: Int,
    val maxNumberGroups: Int,
    val description: String,
    val title: String,
    val dueDate: Timestamp,
) {
    fun isNotValid(): Boolean {
        return maxNumberElems <= 0 ||
            minNumberElems <= 0 ||
            maxNumberGroups <= 0 ||
            description.isBlank() ||
            title.isBlank()
    }
}
