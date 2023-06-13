package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the request to leave a course
 */
data class ClassCodeLeaveCourseDeserialization(
    @JsonProperty("id")val id: Int,
    @JsonProperty("creator")val creator: Int,
    @JsonProperty("state")val state: String = "Pending",
    @JsonProperty("courseId")val courseId: Int,
    @JsonProperty("composite")val composite: Int,
    @JsonProperty("githubUsername")val githubUsername: String,
)
