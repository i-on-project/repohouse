package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the requests that need approval from the database
 */

data class ClassCodeRequestsThatNeedApprovalDeserialization(
    @JsonProperty("joinTeam") val joinTeam: List<ClassCodeJoinTeamDeserialization>,
    @JsonProperty("leaveTeam") val leaveTeam: List<ClassCodeLeaveTeamWithRepoNameDeserialization>,
)
