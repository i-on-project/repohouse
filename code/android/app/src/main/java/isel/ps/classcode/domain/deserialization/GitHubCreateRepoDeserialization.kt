package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the GitHubCreateRepo response from the GitHub API
 */

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitHubCreateRepoDeserialization(
    @JsonProperty("id") val id: Int,
    @JsonProperty("node_id") val nodeId: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("full_name") val fullName: String,
    @JsonProperty("html_url") val htmlUrl: String,
)
