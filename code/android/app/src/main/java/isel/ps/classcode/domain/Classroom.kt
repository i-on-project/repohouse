package isel.ps.classcode.domain

import isel.ps.classcode.domain.dto.LocalClassroomDto
import java.sql.Timestamp


data class Classroom(
    val id: Int,
    val name: String,
    val lastSync: Timestamp,
    val inviteLink: String,
    val isArchived: Boolean,
) {
    fun toLocalClassroomDto(): LocalClassroomDto = LocalClassroomDto(
        id = id,
        name = name,
        lastSync = lastSync,
        inviteLink = inviteLink,
        isArchived = isArchived
    )
}
