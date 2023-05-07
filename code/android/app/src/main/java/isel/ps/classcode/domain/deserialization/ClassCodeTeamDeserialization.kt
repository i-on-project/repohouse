package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

data class ClassCodeTeamDeserialization(
    @JsonProperty("id")val id: Int,
    @JsonProperty("name")val name: String,
    @JsonProperty("isCreated")val isCreated: Boolean,
    @JsonProperty("assignment")val assignment: Int,
)
