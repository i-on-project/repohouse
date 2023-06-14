package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the Create team response from the database
 */

data class ClassCodeCreateTeamDeserialization(
    @JsonProperty("id")val requestId: Int,
    @JsonProperty("creator")val creator: Int,
    @JsonProperty("state")val state: String = "Pending",
    @JsonProperty("composite")val composite: Int,
    @JsonProperty("teamId")val teamId: Int,
    @JsonProperty("teamName")val teamName: String,
    @JsonProperty("githubTeamId")val gitHubTeamId: Int? = null,
)
