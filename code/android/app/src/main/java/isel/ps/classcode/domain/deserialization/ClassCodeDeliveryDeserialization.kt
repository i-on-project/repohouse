package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

data class ClassCodeDeliveryDeserialization(
    @JsonProperty("id") val id: Int,
    @JsonProperty("dueDate") val dueDate: Timestamp,
    @JsonProperty("tagControl") val tagControl: String,
    @JsonProperty("assignmentId") val assignmentId: Int,
)
