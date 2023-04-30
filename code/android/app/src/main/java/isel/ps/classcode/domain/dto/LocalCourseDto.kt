package isel.ps.classcode.domain.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents a course
 */
@Parcelize
data class LocalCourseDto(
    val id: Int,
    val imageUrl: String,
    val name: String,
) : Parcelable
