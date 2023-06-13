package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize a leave team with repo name request
 */
data class ClassCodeLeaveTeamWithRepoNameDeserialization(
    @JsonProperty("leaveTeam") val leaveTeam: ClassCodeLeaveTeamDeserialization,
    @JsonProperty("repoName") val repoName: String,
)
