package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

/**
 * Class used to deserialize the ClassCodeClassroom response from the database
 */
data class ClassCodeClassroomDeserialization(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String,
    @JsonProperty("lastSync") val lastSync: Timestamp,
    @JsonProperty("inviteLink") val inviteLink: String,
    @JsonProperty("isArchived") val isArchived: Boolean,
    @JsonProperty("courseId") val courseId: Int,
)