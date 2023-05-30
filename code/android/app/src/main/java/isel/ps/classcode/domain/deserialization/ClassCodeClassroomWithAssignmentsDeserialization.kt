package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

/**
 * Class used to deserialize the ClassCodeClassroomWithAssignments response from the database
 */
data class ClassCodeClassroomWithAssignmentsDeserialization(
    @JsonProperty("id")val id: Int,
    @JsonProperty("name")val name: String,
    @JsonProperty("isArchived")val isArchived: Boolean,
    @JsonProperty("lastSync")val lastSync: Timestamp,
    @JsonProperty("assignments")val assignments: List<ClassCodeAssignmentDeserialization>,
    @JsonProperty("students")val students: List<ClassCodeStudentDeserialization>,
)
