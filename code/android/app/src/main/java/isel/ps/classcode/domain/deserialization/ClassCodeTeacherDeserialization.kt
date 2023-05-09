package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the ClassCodeTeacher response from the database
 */
data class ClassCodeTeacherDeserialization (
    @JsonProperty("name") val name: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("id") val id: Int,
    @JsonProperty("githubUsername") val githubUsername: String,
    @JsonProperty("githubId") val githubId: Long,
    @JsonProperty("isCreated") val isCreated: Boolean
)