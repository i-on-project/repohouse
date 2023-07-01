package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the request to leave a team
 */

data class ClassCodeCreateRepoDeserialization(
    @JsonProperty("id") val requestId: Int,
    @JsonProperty("creator") val creator: Int,
    @JsonProperty("state") val state: String = "Pending",
    @JsonProperty("composite") val composite: Int,
    @JsonProperty("repoId") val repoId: Int,
    @JsonProperty("repoName") val repoName: String,
)
