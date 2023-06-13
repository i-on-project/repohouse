package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitHubCreateTeamDeserialization(
    @JsonProperty("id") val id: Int,
    @JsonProperty("node_id") val nodeId: String,
    @JsonProperty("url") val url: String,
    @JsonProperty("members_url") val membersUrl: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String?,
    @JsonProperty("html_url") val htmlUrl: String,
    @JsonProperty("permission") val permission: String,
    @JsonProperty("repositories_url") val repositoriesUrl: String,
    @JsonProperty("slug") val slug: String,
    @JsonProperty("privacy") val privacy: String,
)
