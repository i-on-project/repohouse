package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the request to leave a team
 */

data class ClassCodeLeaveTeamDeserialization(
    @JsonProperty("id") val requestId: Int,
    @JsonProperty("creator") val creator: Int,
    @JsonProperty("state") val state: String,
    @JsonProperty("composite") val composite: Int?,
    @JsonProperty("teamId") val teamId: Int,
    @JsonProperty("githubUsername") val githubUsername: String,
    @JsonProperty("membersCount") val membersCount: Int,
    @JsonProperty("teamName") val teamName: String
)
