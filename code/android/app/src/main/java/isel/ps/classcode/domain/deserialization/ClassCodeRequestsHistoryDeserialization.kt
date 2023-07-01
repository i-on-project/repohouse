package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the request history from the database
 */

data class ClassCodeRequestsHistoryDeserialization(
    @JsonProperty("createTeamComposite") val createTeamComposite: ClassCodeCreateTeamCompositeDeserialization,
    @JsonProperty("joinTeam") val joinTeam: List<ClassCodeJoinTeamDeserialization>,
    @JsonProperty("leaveTeam") val leaveTeam: List<ClassCodeLeaveTeamWithRepoNameDeserialization>,
    @JsonProperty("archiveRepo") val archiveRepo: ClassCodeArchiveRepoDeserialization?,
)
