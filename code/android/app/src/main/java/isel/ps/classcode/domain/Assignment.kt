package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeAssignmentDeserialization
import java.sql.Timestamp

/**
 * The class that represents an assignment.
 */
data class Assignment(
    val id: Int,
    val classroomId: Int,
    val description: String,
    val title: String,
) {
    constructor(classCodeAssignmentDeserialization: ClassCodeAssignmentDeserialization) : this(
        id = classCodeAssignmentDeserialization.id,
        classroomId = classCodeAssignmentDeserialization.classroomId,
        description = classCodeAssignmentDeserialization.description,
        title = classCodeAssignmentDeserialization.title,
    )
}
