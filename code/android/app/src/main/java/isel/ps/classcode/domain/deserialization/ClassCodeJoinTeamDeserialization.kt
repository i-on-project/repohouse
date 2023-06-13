package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the request to join a team response from the database
 */

data class ClassCodeJoinTeamDeserialization(
    @JsonProperty("id")val requestId: Int,
    @JsonProperty("creator")val creator: Int,
    @JsonProperty("state")val state: String = "Pending",
    @JsonProperty("composite")val composite: Int?,
    @JsonProperty("teamId")val teamId: Int,
    @JsonProperty("githubUsername")val githubUsername: String,
)
