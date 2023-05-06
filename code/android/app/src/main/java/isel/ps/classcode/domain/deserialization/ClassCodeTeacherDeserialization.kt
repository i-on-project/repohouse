package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

data class ClassCodeTeacherDeserialization (
    @JsonProperty("name") val name: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("id") val id: Int,
    @JsonProperty("githubUsername") val githubUsername: String,
    @JsonProperty("githubId") val githubId: Long,
    @JsonProperty("isCreated") val isCreated: Boolean
)