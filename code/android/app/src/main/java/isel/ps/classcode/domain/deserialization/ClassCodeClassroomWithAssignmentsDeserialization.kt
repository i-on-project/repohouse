package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import isel.ps.classcode.http.hypermedia.SirenEntity
import java.sql.Timestamp

typealias ClassCodeClassroomWithAssignmentsDto = SirenEntity<ClassCodeClassroomWithAssignmentsDeserialization>
val ClassCodeClassroomWithAssignmentsDtoType = SirenEntity.getType<ClassCodeClassroomWithAssignmentsDeserialization>()

data class ClassCodeClassroomWithAssignmentsDeserialization(
    @JsonProperty("id")val id: Int,
    @JsonProperty("name")val name: String,
    @JsonProperty("isArchived")val isArchived: Boolean,
    @JsonProperty("lastSync")val lastSync: Timestamp,
    @JsonProperty("assignments")val assignments: List<ClassCodeAssignmentDeserialization>,
    @JsonProperty("students")val students: List<ClassCodeStudentDeserialization>
)
