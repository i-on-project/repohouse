package com.isel.leic.ps.ion_classcode.domain

data class Classroom(
    val id: Int,
    val name: String,
    val lastSync: String,
    val inviteLink: String,
    val isArchived: Boolean,
    val courseId: Int,
) {
    init {
        require(name.isNotBlank()) { "Classroom name must not be blank" }
        require(inviteLink.isNotBlank()) { "Classroom invite link must not be blank" }
        require(courseId > 0) { "Classroom course id must be greater than 0" }
    }
}
