package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the request to archive a repo
 */

data class ClassCodeArchiveRepoDeserialization(
    @JsonProperty("id") val requestId: Int,
    @JsonProperty("creator") val creator: Int,
    @JsonProperty("state") val state: String,
    @JsonProperty("composite") val composite: Int,
    @JsonProperty("repoId") val repoId: Int,
    @JsonProperty("repoName") val repoName: String,
)
