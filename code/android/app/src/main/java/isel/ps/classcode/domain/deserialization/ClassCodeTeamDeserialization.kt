package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the ClassCodeTeam response from the database
 */
data class ClassCodeTeamDeserialization(
    @JsonProperty("id")val id: Int,
    @JsonProperty("name")val name: String,
    @JsonProperty("isCreated")val isCreated: Boolean,
    @JsonProperty("assignment")val assignment: Int,
)
