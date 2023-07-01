package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

/**
 * Class used to deserialize the ClassCodeAssignment response from the database
 */

data class ClassCodeAssignmentDeserialization(
    @JsonProperty("id")val id: Int,
    @JsonProperty("classroomId")val classroomId: Int,
    @JsonProperty("minElemsPerGroup")val minElemsPerGroup: Int,
    @JsonProperty("maxElemsPerGroup")val maxElemsPerGroup: Int,
    @JsonProperty("maxNumberGroups")val maxNumberGroups: Int,
    @JsonProperty("releaseDate")val releaseDate: Timestamp,
    @JsonProperty("description")val description: String,
    @JsonProperty("title")val title: String,
)
