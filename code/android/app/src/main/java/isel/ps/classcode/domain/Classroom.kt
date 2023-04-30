package isel.ps.classcode.domain

import java.security.Timestamp

data class Classroom(
    val id: Int,
    val name: String,
    val lastSync: Timestamp,
    val inviteLink: String,
    val isArchived: Boolean,
)
