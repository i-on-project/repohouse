package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the ClassCodeCreateTeamComposite response from the database
 */

data class ClassCodeCreateTeamCompositeDeserialization(
    @JsonProperty("compositeState") val compositeState: String,
    @JsonProperty("createTeam") val createTeam: ClassCodeCreateTeamDeserialization,
    @JsonProperty("joinTeam") val joinTeam: ClassCodeJoinTeamDeserialization,
    @JsonProperty("createRepo") val createRepo: ClassCodeCreateRepoDeserialization,
)
