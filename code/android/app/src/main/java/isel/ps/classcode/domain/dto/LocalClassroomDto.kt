package isel.ps.classcode.domain.dto

import android.os.Parcelable
import isel.ps.classcode.domain.Classroom
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

/**
 * The class that represents a classroom when passing info from a activity to another.
 */
@Parcelize
data class LocalClassroomDto(
    val id: Int,
    val name: String,
    val lastSync: Timestamp,
    val inviteLink: String,
    val isArchived: Boolean,
    val courseId: Int,
    val courseName: String
): Parcelable {

    /**
     * Function to pass a local classroom dto to a classroom.
     */
    fun toClassroom(): Classroom = Classroom(
        id = id,
        name = name,
        lastSync = lastSync,
        inviteLink = inviteLink,
        isArchived = isArchived,
        courseId = courseId
    )
}
