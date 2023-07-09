package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeLeaveCourseDeserialization

/**
 * Represents a ClassCode Leave Course
 */
data class LeaveCourse(
    val requestId: Int,
    val creator: Int,
    val state: String = "Pending",
    val courseId: Int,
    val composite: Int,
    val githubUsername: String,
) {
    constructor(deserialization: ClassCodeLeaveCourseDeserialization) : this(
        requestId = deserialization.id,
        creator = deserialization.creator,
        state = deserialization.state,
        courseId = deserialization.courseId,
        composite = deserialization.composite,
        githubUsername = deserialization.githubUsername,
    )
}
