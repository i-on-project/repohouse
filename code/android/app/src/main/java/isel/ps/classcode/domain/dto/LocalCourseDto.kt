package isel.ps.classcode.domain.dto

import android.os.Parcelable
import isel.ps.classcode.domain.Course
import kotlinx.parcelize.Parcelize

/**
 * Represents a course when passing info from a activity to another.
 */
@Parcelize
data class LocalCourseDto(
    val id: Int,
    val orgUrl: String,
    val orgId: Long,
    val name: String,
) : Parcelable {

    /**
     * Function to pass a local course dto to a course.
     */
    fun toCourseDto(): Course = Course(id = id, name = name, orgId = orgId,orgUrl = orgUrl)
}
