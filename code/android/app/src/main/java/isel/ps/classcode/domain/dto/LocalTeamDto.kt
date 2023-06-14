package isel.ps.classcode.domain.dto

import android.os.Parcelable
import isel.ps.classcode.domain.Team
import kotlinx.parcelize.Parcelize

/**
 * Represents a team when passing info from a activity to another.
 */
@Parcelize
data class LocalTeamDto(
    val id: Int,
    val name: String,
    val isCreated: Boolean,
    val isClosed: Boolean,
    val assignment: Int,
    val courseId: Int,
    val courseName: String,
    val classroomId: Int,
) : Parcelable {

    /**
     * Function to pass a local team dto to a team.
     */

    fun toTeam(): Team = Team(
        id = id,
        name = name,
        isCreated = isCreated,
        isClosed = isClosed,
        assignment = assignment,
    )
}
