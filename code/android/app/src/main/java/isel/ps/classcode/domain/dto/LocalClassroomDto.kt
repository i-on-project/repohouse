package isel.ps.classcode.domain.dto

import android.os.Parcelable
import isel.ps.classcode.domain.Classroom
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class LocalClassroomDto(
    val id: Int,
    val name: String,
    val lastSync: Timestamp,
    val inviteLink: String,
    val isArchived: Boolean,
): Parcelable {
    fun toClassroom(): Classroom = Classroom(
        id = id,
        name = name,
        lastSync = lastSync,
        inviteLink = inviteLink,
        isArchived = isArchived
    )
}
